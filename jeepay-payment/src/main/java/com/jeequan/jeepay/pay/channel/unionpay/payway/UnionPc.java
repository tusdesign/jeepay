package com.jeequan.jeepay.pay.channel.unionpay.payway;

import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;

import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.model.params.unionpay.UnionPayConfig;
import com.jeequan.jeepay.core.model.params.unionpay.UnionPayNormalMchParams;
import com.jeequan.jeepay.pay.channel.unionpay.UnionpayPaymentService;
import com.jeequan.jeepay.pay.channel.unionpay.utils.UnionPayUtil;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.AbstractRS;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.rqrs.payorder.UnifiedOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaPcOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaPcOrderRS;
import com.jeequan.jeepay.pay.util.ApiResBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service("unionpayPaymentByUnionPcService")
public class UnionPc extends UnionpayPaymentService {

    @Autowired
    private UnionPayUtil unionPayUtil;

    @Override
    public String preCheck(UnifiedOrderRQ bizRQ, PayOrder payOrder) {
        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {

        UnionPayNormalMchParams params = (UnionPayNormalMchParams) configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), getIfCode());

        ChinaPcOrderRQ bizRQ = (ChinaPcOrderRQ) rq;
        ChinaPcOrderRS res = ApiResBuilder.buildSuccess(ChinaPcOrderRS.class);
        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        res.setChannelRetMsg(channelRetMsg);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

        Map<String, Object> paramMap = new TreeMap<>();
        Date nowDate = new Date();
        paramMap.put("Version", params.getPayVersion());
        paramMap.put("AccessType", "0"); //接入类型  0：商户身份接入（默认）1：机构身份接入
        paramMap.put("MerId", params.getMchId());
        paramMap.put("MerOrderNo", bizRQ.getMchOrderNo());
        paramMap.put("TranDate", dateFormat.format(new Date()));
        paramMap.put("TranTime", timeFormat.format(new Date()));
        paramMap.put("OrderAmt", String.valueOf(bizRQ.getAmount()));//单位：分
        paramMap.put("BusiType", "0001");//固定值:表示银行卡的快捷支付

        if (StringUtils.isNotEmpty(bizRQ.getBody())) {
            paramMap.put("CommodityMsg", bizRQ.getBody());
        }
        if (StringUtils.isNotEmpty(getNotifyUrl())) {
            paramMap.put("MerBgUrl", getNotifyUrl());
        }
        if (StringUtils.isNotEmpty(bizRQ.getReturnUrl())) {
            paramMap.put("MerPageUrl", bizRQ.getReturnUrl());
        }
        if (StringUtils.isNotEmpty(bizRQ.getClientIp())) {
            paramMap.put("RemoteAddr", bizRQ.getClientIp());
        }

        boolean initResult = unionPayUtil.init(params);
        if (initResult) {

            SecssUtil secssUtil = unionPayUtil.getSecssUtil();
            secssUtil.sign(paramMap);
            if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {

                log.error(secssUtil.getErrCode() + "=" + secssUtil.getErrMsg());
                channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
                channelRetMsg.setChannelErrCode("9999");
                channelRetMsg.setChannelErrMsg(secssUtil.getErrMsg());
            }

            String signature = secssUtil.getSign();
            paramMap.put("Signature", signature);

            //System.out.println("####################请求总参数####################");

            String buildRequest = unionPayUtil.buildRequest(paramMap, unionPayUtil.getPayUrl(params.getFrontPayUrl() + UnionPayConfig.FRONTPAYPATH), "post", "确定");

            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.WAITING);
            res.setFormContent(buildRequest);

        } else {
            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
            channelRetMsg.setChannelErrCode("9999");
            channelRetMsg.setChannelErrMsg("UnionPay初始化错误");
        }

        res.setPayData(res.buildPayData());
        res.setPayDataType(res.buildPayDataType());
        return res;
    }

}
