package com.jeequan.jeepay.pay.channel.qidipay.payway;

import ch.qos.logback.core.util.TimeUtil;
import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;

import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.pay.channel.qidipay.QidipayPaymentService;
import com.jeequan.jeepay.pay.channel.qidipay.utils.ChinaPayUtil;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.AbstractRS;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.rqrs.payorder.UnifiedOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.AliBarOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.AliBarOrderRS;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaPcOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaPcOrderRS;
import com.jeequan.jeepay.pay.util.ApiResBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service("chinaPaymentByChinaPcService")
public class ChinaPc extends QidipayPaymentService {

    private static final String url = "https://sfj-test.chinapay.com/dac/BatchPayTrade";
    private static final String frontPayUrl = "https://newpayment-test.chinapay.com/CTITS/service/rest/page/nref/000000000017/0/0/0/0/0";
    private static final String endPayUrl = "https://newpayment-test.chinapay.com/CTITS/service/rest/forward/syn/000000000017/0/0/0/0/0";

    @Override
    public String preCheck(UnifiedOrderRQ bizRQ, PayOrder payOrder) {
        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {

        log.info("####################开始支付####################");

        ChinaPcOrderRQ bizRQ = (ChinaPcOrderRQ) rq;
        ChinaPcOrderRS res = ApiResBuilder.buildSuccess(ChinaPcOrderRS.class);
        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        res.setChannelRetMsg(channelRetMsg);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

        Map<String, Object> paramMap = new TreeMap<>();
        Date nowDate = new Date();
        paramMap.put("Version", "20140728");
        paramMap.put("AccessType","0"); //接入类型  0：商户身份接入（默认）1：机构身份接入
        paramMap.put("MerId", "000091908248636");
        paramMap.put("MerOrderNo", "换成订单流水号");
        paramMap.put("TranDate", dateFormat.format(new Date()));
        paramMap.put("TranTime", timeFormat.format(new Date()));
        paramMap.put("OrderAmt", "1");//单位：分
        paramMap.put("BusiType", "0001");//业务类型，固定值
        paramMap.put("CommodityMsg","大米100斤");

        paramMap.put("MerBgUrl", getNotifyUrl());
        paramMap.put("MerPageUrl", bizRQ.getReturnUrl());
        paramMap.put("RemoteAddr", "xxxIP");

        System.out.println("==============订单号===========:"+paramMap.get("MerOrderNo"));

        SecssUtil secssUtil = ChinaPayUtil.secssUtil;
        secssUtil.sign(paramMap);
        if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode()))
        {
            log.error(secssUtil.getErrCode() + "=" + secssUtil.getErrMsg());

            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
            channelRetMsg.setChannelErrCode("9999");
            channelRetMsg.setChannelErrMsg(secssUtil.getErrMsg());
        }
        String signature = secssUtil.getSign();
        paramMap.put("Signature", signature);

        System.out.println("####################请求总参数####################");
        System.out.println(paramMap);
        //必须构建成【自动提交form表单】html，返回商城前端自动跳转到网银支付页面
        String buildRequest = ChinaPayUtil.buildRequest(paramMap,frontPayUrl, "post", "确定");


        //请求--不能直接使用http工具发起支付请求，需要构建form表单请求自动提交
        //String result = HttpUtils.send(frontPayUrl, paramMap);
        //System.out.println("返回结果："+result);
        //return "toPay";

        channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
        res.setFormContent(buildRequest);
        return res;
    }

}
