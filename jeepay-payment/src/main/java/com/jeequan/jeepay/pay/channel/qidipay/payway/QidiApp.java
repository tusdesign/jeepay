package com.jeequan.jeepay.pay.channel.qidipay.payway;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.exception.BizException;
import com.jeequan.jeepay.core.utils.AmountUtil;
import com.jeequan.jeepay.pay.channel.alipay.AlipayKit;
import com.jeequan.jeepay.pay.channel.qidipay.QidipayPaymentService;
import com.jeequan.jeepay.pay.exception.ChannelException;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.AbstractRS;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.rqrs.payorder.UnifiedOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.*;
import com.jeequan.jeepay.pay.util.ApiResBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service("qidiPaymentByQidiAppService")
public class QidiApp extends QidipayPaymentService {

    @Override
    public String preCheck(UnifiedOrderRQ rq, PayOrder payOrder) {

//        QiDiAppOrderRQ bizRQ = (QiDiAppOrderRQ) rq;
//        if(StringUtils.isEmpty(bizRQ.getAuthCode())){
//            throw new BizException("用户支付条码[authCode]不可为空");
//        }
        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrder payOrder, MchAppConfigContext mchAppConfigContext){

        QiDiAppOrderRQ bizRQ = (QiDiAppOrderRQ) rq;

        // 构造函数响应数据
        AliPcOrderRS res = ApiResBuilder.buildSuccess(AliPcOrderRS.class);
        res.setFormContent("");
        res.setPayUrl("");

        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        res.setChannelRetMsg(channelRetMsg);

        //放置 响应数据
        channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
        return res;
    }
}
