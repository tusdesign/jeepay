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
        paramMap.put("TranType", UnionPayConfig.TRAN_TYPE.TRAN_SELECT);//交易类型，固定值：0502表示订单查询
        paramMap.put("BusiType", UnionPayConfig.BUSINESS_TYPE);//业务类型，固定值表示在线订单
        paramMap.put("MerOrderNo", payOrder.getMchOrderNo());
        paramMap.put("MerId", payOrder.getMchNo());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd"); //原交易日期，格式: yyyyMMdd
        paramMap.put("TranDate", dateFormat.format(payOrder.getCreatedAt()));

        try {
            if (chinaPayUtil.init(normalMchParams)) {

                SecssUtil secssUtil = chinaPayUtil.getSecssUtil();
                secssUtil.sign(paramMap);
                if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {
                    log.error("ChinaPay签名失败：" + secssUtil.getErrCode() + "=" + secssUtil.getErrMsg());
                    return ChannelRetMsg.confirmFail(secssUtil.getErrCode(), secssUtil.getErrMsg());
                }

                paramMap.put("Signature", secssUtil.getSign());
                String resJSON = HttpUtil.post(chinaPayUtil.getPayUrl(normalMchParams.getBgPayUrl()) + UnionPayConfig.BACKPAY_PATH, paramMap);

                log.info("查询订单 payorderId:{}, 返回结果:{}", payOrder.getPayOrderId(), resJSON);
                if (StringUtils.isEmpty(resJSON)) {
                    return ChannelRetMsg.waiting(); //查询处理中
                }

                Map<String, Object> resultMap = chinaPayUtil.strToMap(resJSON); //解析同步应答字段
                secssUtil.verify(resultMap);
                if (SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {
                    return ChannelRetMsg.confirmSuccess(resultMap.get("MerOrderNo").toString());  //支付成功

                } else {
                    String outTradeNo = resultMap.get("MerOrderNo") == null ? "" : resultMap.get("MerOrderNo").toString(); // 渠道订单号
                    log.error("UnionPay返回的应答数据【验签】失败:" + secssUtil.getErrCode() + "=" + secssUtil.getErrMsg() + "支付明细编号为：" + outTradeNo);

                    return ChannelRetMsg.confirmFail(resultMap.get("MerOrderNo").toString(), secssUtil.getErrCode(), secssUtil.getErrMsg());
                }
            }
            return ChannelRetMsg.sysError("UnionPay配置参数初始化错误");

        } catch (Exception ex) {

            log.error("UnionPay查询失败:" + ex.getMessage() + "支付明细编号为：" + payOrder.getMchOrderNo());
            return ChannelRetMsg.waiting();
        }
    }

}
