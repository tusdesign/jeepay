package com.jeequan.jeepay.pay.channel.qidipay.payway;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.model.params.qidipay.QidipayNormalMchParams;
import com.jeequan.jeepay.pay.channel.qidipay.QidipayPaymentService;
import com.jeequan.jeepay.pay.channel.qidipay.utils.ChinaPayUtil;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.AbstractRS;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.rqrs.payorder.UnifiedOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaPcOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaPcOrderRS;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaQrOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaQrOrderRS;
import com.jeequan.jeepay.pay.util.ApiResBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service("chinaPaymentByChinaQrService")
public class ChinaQr extends QidipayPaymentService {

    @Override
    public String preCheck(UnifiedOrderRQ bizRQ, PayOrder payOrder) {
        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {

        QidipayNormalMchParams params = (QidipayNormalMchParams) configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), getIfCode());

        ChinaQrOrderRQ bizRQ = (ChinaQrOrderRQ) rq;
        ChinaQrOrderRS res = ApiResBuilder.buildSuccess(ChinaQrOrderRS.class);
        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        res.setChannelRetMsg(channelRetMsg);

        //模拟测试订单信息
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

        Date current = new Date();
        Map<String, String> orderReserveMap = new HashMap<>();
        orderReserveMap.put("OrderType", "0001");
        orderReserveMap.put("OrderValidTime", format.format(current));
        orderReserveMap.put("qrPattern", "link");
        String orderReserve = JSON.toJSONString(orderReserveMap);

        Map<String, Object> submitFromData = new HashMap();
        submitFromData.put("Version", params.getPayVersion());
        submitFromData.put("AccessType", "0");
        submitFromData.put("MerId", params.getMchId());
        submitFromData.put("MerOrderNo", bizRQ.getMchOrderNo());
        submitFromData.put("TranDate", dateFormat.format(new Date()));
        submitFromData.put("TranTime", timeFormat.format(new Date()));
        submitFromData.put("OrderAmt", String.valueOf(bizRQ.getAmount()));
        submitFromData.put("TranType", "0009");
        submitFromData.put("BusiType", "0001");
        submitFromData.put("CurryNo", "CNY");

        submitFromData.put("MerPageUrl", bizRQ.getReturnUrl());
        submitFromData.put("MerBgUrl", getNotifyUrl());
        submitFromData.put("MerResv", "productId");
        submitFromData.put("PayTimeOut", "30");
        submitFromData.put("OrderReserved", orderReserve);
        submitFromData.put("CommodityMsg", bizRQ.getBody());

        SecssUtil secssUtil = ChinaPayUtil.init(params);
        secssUtil.sign(submitFromData);
        if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {
            //log.error(secssUtil.getErrCode() + "=" + secssUtil.getErrMsg());
            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
            channelRetMsg.setChannelErrCode("9999");
            channelRetMsg.setChannelErrMsg(secssUtil.getErrMsg());
        }
        secssUtil.sign(submitFromData);
        String signature = secssUtil.getSign();
        submitFromData.put("Signature", signature);

        String httpRes = HttpUtil.post("chinaPayConstants.PAY_SEND_URL", submitFromData, 60000);

        String codeUrl = StringUtils.EMPTY;
        Map<String, String> resultMap = ChinaPayUtil.getResponseMap(httpRes);

        String respCode = resultMap.get("respCode");//应答码
        String respMsg = resultMap.get("respMsg");//应答信息

        try {

            if ("0000".equals(respCode)) {
                boolean verifyFlag = ChinaPayUtil.verifyNotify(resultMap, params);
                if (!verifyFlag) {
                    //log.error("ChinaPay支付查询--返回数据验签失败！");
                    res.setChannelRetMsg(channelRetMsg.confirmFail("201", "ChinaPay支付查询--返回数据验签失败！"));
                    return res;
                }
                if ("00".equals(secssUtil.getErrCode())) {
                    String payReserved = (String) resultMap.get("PayReserved");
                    Map payReservedMap = JSON.parseObject(payReserved, Map.class);
                    codeUrl = (String) payReservedMap.get("QrCode");
                    codeUrl = URLDecoder.decode(codeUrl, "UTF-8");
                }
            }

            if (StringUtils.isNotBlank(codeUrl)) {

                BufferedImage bufferedImage = QrCodeUtil.generate(codeUrl, 300, 300);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", outStream);
                byte[] bytes = outStream.toByteArray();
                String imageBase64 = cn.hutool.core.codec.Base64.encode(bytes);
                codeUrl = "data:image/png;base64," + imageBase64;

                if (CS.PAY_DATA_TYPE.CODE_IMG_URL.equals(res.getPayDataType())) {
                    res.setCodeImgUrl(sysConfigService.getDBApplicationConfig().genScanImgUrl(codeUrl));
                } else {
                    res.setCodeUrl(codeUrl);
                }
                res.setPayData(res.buildPayData());
                channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.WAITING);

            } else {
                channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
                channelRetMsg.setChannelErrCode(respCode);
                channelRetMsg.setChannelErrMsg(respMsg);
            }

        } catch (Exception e) {
            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
            channelRetMsg.setChannelErrCode(respCode);
            channelRetMsg.setChannelErrMsg(respMsg);
        }
        return res;
    }

}
