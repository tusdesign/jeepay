package com.jeequan.jeepay.pay.channel.qidipay;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.model.params.qidipay.QidipayNormalMchParams;
import com.jeequan.jeepay.core.model.params.xxpay.XxpayNormalMchParams;
import com.jeequan.jeepay.core.utils.JeepayKit;
import com.jeequan.jeepay.pay.channel.IPayOrderQueryService;
import com.jeequan.jeepay.pay.channel.xxpay.XxpayKit;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.service.ConfigContextQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        QidipayNormalMchParams xxpayParams = (QidipayNormalMchParams) configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo()
                , mchAppConfigContext.getAppId()
                , getIfCode());

        Map<String, Object> paramMap = new TreeMap();// 接口类型
        paramMap.put("mchId", xxpayParams.getMchId());
        paramMap.put("mchOrderNo", payOrder.getPayOrderId());
        String sign = XxpayKit.getSign(paramMap, xxpayParams.getKey());
        paramMap.put("sign", sign);
        String resStr = "";
        String queryPayOrderUrl = XxpayKit.getQueryPayOrderUrl(xxpayParams.getPayUrl()) + "?" + JeepayKit.genUrlParams(paramMap);
        try {
            log.info("支付查询[{}]参数：{}", getIfCode(), queryPayOrderUrl);
            //resStr = HttpUtil.createPost(queryPayOrderUrl).timeout(60 * 1000).execute().body();
            log.info("支付查询[{}]结果：{}", getIfCode(), resStr);
        } catch (Exception e) {
            log.error("http error", e);
        }
//        if(StringUtils.isEmpty(resStr)) {
//            return ChannelRetMsg.waiting(); //支付中
//        }
//        JSONObject resObj = JSONObject.parseObject(resStr);
//        if(!"0".equals(resObj.getString("retCode"))){
//            return ChannelRetMsg.waiting(); //支付中
//        }
//        // 支付状态,0-订单生成,1-支付中,2-支付成功,3-业务处理完成
//        String status = resObj.getString("status");
//        if("2".equals(status) || "3".equals(status)) {
//            return ChannelRetMsg.confirmSuccess(resObj.getString("channelOrderNo"));  //支付成功
//        }
//        return ChannelRetMsg.waiting(); //支付中

        ChannelRetMsg channelResult = new ChannelRetMsg();
        channelResult.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
        return channelResult;

    }
}
