package com.jeequan.jeepay.pay.channel.unionpay;

import cn.hutool.http.HttpUtil;
import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.exception.ResponseException;
import com.jeequan.jeepay.core.model.params.unionpay.UnionPayConfig;
import com.jeequan.jeepay.core.model.params.unionpay.UnionPayNormalMchParams;
import com.jeequan.jeepay.pay.channel.IPayOrderQueryService;
import com.jeequan.jeepay.pay.channel.unionpay.utils.UnionPayUtil;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.service.ConfigContextQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UnionpayOrderQueryService implements IPayOrderQueryService {

    @Autowired
    private UnionPayUtil chinaPayUtil;

    @Autowired
    private ConfigContextQueryService configContextQueryService;

    @Override
    public String getIfCode() {
        return CS.IF_CODE.UNIONPAY;
    }

    @Override
    public ChannelRetMsg query(PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {

        UnionPayNormalMchParams normalMchParams = (UnionPayNormalMchParams) configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo()
                , mchAppConfigContext.getAppId()
                , getIfCode());

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("Version", normalMchParams.getPayVersion());
        paramMap.put("TranType", "0502");//交易类型，固定值：0502
        paramMap.put("BusiType", "0001");//业务类型，固定值
        paramMap.put("MerOrderNo", payOrder.getMchOrderNo());
        paramMap.put("MerId", payOrder.getMchNo());

        // 原交易日期，格式: yyyyMMdd TODO 替换
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        paramMap.put("TranDate", dateFormat.format(payOrder.getCreatedAt()));

        if (chinaPayUtil.init(normalMchParams)) {

            SecssUtil secssUtil = chinaPayUtil.getSecssUtil();
            secssUtil.sign(paramMap);

            if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {
                log.error("ChinaPay签名失败：" + secssUtil.getErrCode() + "=" + secssUtil.getErrMsg());
                return ChannelRetMsg.confirmFail(secssUtil.getErrCode(), secssUtil.getErrMsg());
            }

            // TODO 同步请求
            paramMap.put("Signature", secssUtil.getSign());
            String resp = HttpUtil.post( chinaPayUtil.getPayUrl(normalMchParams.getBgPayUrl())+ UnionPayConfig.BGPAYPATH, paramMap);
            log.info("################交易查询结果：{}", resp);

            //解析同步应答字段
            Map<String, String> resultMap = chinaPayUtil.getResponseMap(resp);

            //返回数据验签
            String sign = resultMap.get("Signature"); //返回数据验签
            if (StringUtils.isEmpty(sign)) {
                secssUtil.verify(resultMap);
            }
            if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {
                String outTradeNo = resultMap.get("MerOrderNo") == null ? "" : resultMap.get("MerOrderNo"); // 渠道订单号
                log.error("ChinaPay返回的应答数据【验签】失败:" + secssUtil.getErrCode() + "=" + secssUtil.getErrMsg() + "支付明细编号为：" + outTradeNo);
                throw ResponseException.buildText("ERROR");
            }

            ChannelRetMsg channelResult = new ChannelRetMsg();
            channelResult.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
            return channelResult;
        }
        return ChannelRetMsg.confirmFail();

    }

}
