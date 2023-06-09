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
//        QidipayNormalMchParams qidipayNormalMchParams = (QidipayNormalMchParams) configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo()
//                , mchAppConfigContext.getAppId()
//                , getIfCode());

        ChannelRetMsg channelResult = new ChannelRetMsg();
        channelResult.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
        return channelResult;

    }
}
