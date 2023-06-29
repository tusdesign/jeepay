package com.jeequan.jeepay.pay.channel.unionpay.payway;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.model.params.unionpay.UnionPayConfig;
import com.jeequan.jeepay.core.model.params.unionpay.UnionPayNormalMchParams;
import com.jeequan.jeepay.pay.channel.unionpay.UnionpayPaymentService;
import com.jeequan.jeepay.pay.channel.unionpay.utils.*;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.AbstractRS;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.rqrs.payorder.UnifiedOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaQrOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaQrOrderRS;
import com.jeequan.jeepay.pay.util.ApiResBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("unionpayPaymentByUnionQrService")
public class UnionQr extends UnionpayPaymentService {

    @Autowired
    private UnionPayUtil chinaPayUtil;

    @Override
    public String preCheck(UnifiedOrderRQ bizRQ, PayOrder payOrder) {
        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {

        UnionPayNormalMchParams params = (UnionPayNormalMchParams) configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), getIfCode());

        ChinaQrOrderRQ bizRQ = (ChinaQrOrderRQ) rq.buildBizRQ();
        ChinaQrOrderRS res = ApiResBuilder.buildSuccess(ChinaQrOrderRS.class);
        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        res.setChannelRetMsg(channelRetMsg);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

        Map<String, String> orderReserveMap = new HashMap<>();
        orderReserveMap.put("OrderType", "0001");//0001表示订单二维码
        orderReserveMap.put("OrderValidTime", DateUtil.getNextDate(DateUtil.getToday("yyyyMMdd"), 1) + "235959");

        if (StringUtils.isNotEmpty(bizRQ.getPayDataType())) {
            if (bizRQ.getPayDataType().equals(CS.PAY_DATA_TYPE.PAY_URL)) {
                orderReserveMap.put("qrPattern", "link");
            } else if (bizRQ.getPayDataType().equals(CS.PAY_DATA_TYPE.CODE_URL)) {
                orderReserveMap.put("qrPattern", "link");
            } else if (bizRQ.getPayDataType().equals(CS.PAY_DATA_TYPE.CODE_IMG_URL)) {
                orderReserveMap.put("qrPattern", "image");
            }
        } else {
            bizRQ.setPayDataType(CS.PAY_DATA_TYPE.CODE_IMG_URL);
            orderReserveMap.put("qrPattern", "image");
        }

        if (StringUtils.isEmpty(bizRQ.getQrCodeProvider())) {
            orderReserveMap.put("QrCodeProvider", "0001");
        } else {
            orderReserveMap.put("QrCodeProvider", "0002");
        }

        String orderReserve = JSON.toJSONString(orderReserveMap);

        Map<String, Object> submitFromData = new HashMap();
        submitFromData.put("Version", params.getPayVersion());
        submitFromData.put("AccessType", UnionPayConfig.ACCESS_TYPE_MCH);//接入类型 0:商户身份接入 1:机构身份接入
        submitFromData.put("MerId", params.getMchId());
        submitFromData.put("MerOrderNo", bizRQ.getMchOrderNo());
        submitFromData.put("TranDate", dateFormat.format(new Date()));
        submitFromData.put("TranTime", timeFormat.format(new Date()));
        submitFromData.put("OrderAmt", String.valueOf(bizRQ.getAmount()));
        submitFromData.put("TranType", UnionPayConfig.TRAN_TYPE.TRAN_QR);//0009表示银联二维码支付方式
        submitFromData.put("BusiType", UnionPayConfig.BUSINESS_TYPE);//固定值
        submitFromData.put("CurryNo", "CNY");
        submitFromData.put("MerPageUrl", getReturnUrl(bizRQ.getMchOrderNo())); //前台页面通知地址
        submitFromData.put("MerBgUrl", getNotifyUrl(bizRQ.getMchOrderNo())); //异步信息回调地址

        if (StringUtils.isNotEmpty(bizRQ.getBody())) {
            submitFromData.put("CommodityMsg", bizRQ.getBody()); //订单描述信息
        }
        submitFromData.put("MerResv", payOrder.getPayOrderId());
        submitFromData.put("PayTimeOut", "30");//订单支付有效时间:30分钟
        submitFromData.put("OrderReserved", orderReserve);//支付拓展信息

        try {

            Boolean initResult = chinaPayUtil.init(params);
            if (initResult) {
                SecssUtil secssUtil = chinaPayUtil.getSecssUtil();
                secssUtil.sign(submitFromData);

                if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {

                    log.error(secssUtil.getErrCode() + "=" + secssUtil.getErrMsg());
                    channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
                    channelRetMsg.setChannelErrCode(secssUtil.getErrCode());
                    channelRetMsg.setChannelErrMsg(secssUtil.getErrMsg());
                }

                String signature = secssUtil.getSign();
                submitFromData.put("Signature", signature);

                String codeUrl = StringUtils.EMPTY;
                String payUrl = chinaPayUtil.getPayUrl(params.getQrPayUrl()) + UnionPayConfig.QRPAY_PATH;

                String httpResponse = HttpUtil.post(payUrl, submitFromData, 60000);

                Map<String, Object> resultMap = chinaPayUtil.strToMap(httpResponse);
                Object respCode = resultMap.get("respCode");//应答码
                Object respMsg = resultMap.get("respMsg");//应答信息

                if (UnionPayConfig.RESPONSE_STATUS.equals(respCode)) {

                    secssUtil.verify(resultMap);
                    if (SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {

                        String payReserved = (String) resultMap.get("PayReserved");
                        Map payReservedMap = JSON.parseObject(payReserved, Map.class);
                        codeUrl = (String) payReservedMap.get("QrCode");

                        if (CS.PAY_DATA_TYPE.PAY_URL.equals(bizRQ.getPayDataType())) {
                            codeUrl = URLDecoder.decode(codeUrl, "UTF-8");
                            res.setPayUrl(codeUrl);

                        } else if (CS.PAY_DATA_TYPE.CODE_URL.equals(bizRQ.getPayDataType())) {

                            if (StringUtils.isNotBlank(codeUrl)) {
                                codeUrl = URLDecoder.decode(codeUrl, "UTF-8");
                                BufferedImage bufferedImage = QrCodeUtil.generate(codeUrl, 300, 300);
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                ImageIO.write(bufferedImage, "png", outStream);
                                byte[] bytes = outStream.toByteArray();
                                String imageBase64 = cn.hutool.core.codec.Base64.encode(bytes);
                                codeUrl = "data:image/png;base64," + imageBase64;

                                res.setCodeImgUrl(codeUrl);
                            }
                        } else if (CS.PAY_DATA_TYPE.CODE_IMG_URL.equals(bizRQ.getPayDataType())) {

                            if (StringUtils.isNotBlank(codeUrl)) {
                                String imageBytes = "data:image/png;base64," + codeUrl;
                                res.setCodeImgUrl(imageBytes);
                            }
                        }else{
                            codeUrl = URLDecoder.decode(codeUrl, "UTF-8");
                            res.setPayUrl(codeUrl);
                        }
                        channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.WAITING);
                    }
                } else {

                    channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
                    channelRetMsg.setChannelErrCode(respCode.toString());
                    channelRetMsg.setChannelErrMsg(respMsg.toString());
                }
            }
            res.setPayDataType(res.buildPayDataType());
            res.setPayData(res.buildPayData());

        } catch (Exception ex) {

            log.error("支付过程中出现错误：" + ex.getMessage() + "订单号:" + rq.getMchOrderNo());

            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
            channelRetMsg.setChannelErrCode("9999");
            channelRetMsg.setChannelErrMsg("支付过程中出现错误" + ex.getMessage());
        }
        return res;
    }

}
