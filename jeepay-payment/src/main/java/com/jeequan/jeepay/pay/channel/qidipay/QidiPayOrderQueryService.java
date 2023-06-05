package com.jeequan.jeepay.pay.channel.qidipay;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.model.params.qidipay.QidipayNormalMchParams;
import com.jeequan.jeepay.core.model.params.xxpay.XxpayNormalMchParams;
import com.jeequan.jeepay.core.utils.JeepayKit;
import com.jeequan.jeepay.pay.channel.IPayOrderQueryService;
import com.jeequan.jeepay.pay.channel.qidipay.utils.ChinaPayUtil;
import com.jeequan.jeepay.pay.channel.xxpay.XxpayKit;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.service.ConfigContextQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
@Slf4j
public class QidiPayOrderQueryService implements IPayOrderQueryService {

    @Autowired
    private ConfigContextQueryService configContextQueryService;


    @Override
    public String getIfCode() {
        return CS.IF_CODE.QIDIPAY;
    }

    @Override
    public ChannelRetMsg query(PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {

        QidipayNormalMchParams normalMchParams = (QidipayNormalMchParams) configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo()
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

        SecssUtil secssUtil = ChinaPayUtil.init(normalMchParams);
        secssUtil.sign(paramMap);

        if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {

            log.error("ChinaPay签名失败：" + secssUtil.getErrCode() + "=" + secssUtil.getErrMsg());
            return ChannelRetMsg.confirmFail(secssUtil.getErrCode(), secssUtil.getErrMsg());
        }

        // TODO 同步请求
        paramMap.put("Signature", secssUtil.getSign());
        String resp = HttpUtil.post("payQueryUrl", paramMap);
        log.info("################交易查询结果：{}", resp);

        //解析同步应答字段
        Map<String, String> resultMap = ChinaPayUtil.getResponseMap(resp);

        //返回数据验签
        boolean verifyFlag = ChinaPayUtil.verifyNotify(resultMap, normalMchParams);
        if (!verifyFlag) {
            log.error("ChinaPay支付查询--返回数据验签失败！");
            return ChannelRetMsg.confirmFail("201", "ChinaPay支付查询--返回数据验签失败！");
        }

        ChannelRetMsg channelResult = new ChannelRetMsg();
        channelResult.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
        return channelResult;
    }


}
