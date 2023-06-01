package com.jeequan.jeepay.pay.channel.qidipay.payway;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.model.params.qidipay.QidipayNormalMchParams;
import com.jeequan.jeepay.pay.channel.qidipay.QidipayPaymentService;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.AbstractRS;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.rqrs.payorder.UnifiedOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaPcOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaPcOrderRS;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaQrOrderRQ;
import com.jeequan.jeepay.pay.rqrs.payorder.payway.ChinaQrOrderRS;
import com.jeequan.jeepay.pay.util.ApiResBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service("chinaPaymentByChinaQrService")
public class ChinaQr extends QidipayPaymentService {

    @Override
    public String preCheck(UnifiedOrderRQ bizRQ, PayOrder payOrder) {
        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {

        QidipayNormalMchParams params = (QidipayNormalMchParams)configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), getIfCode());

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
        orderReserveMap.put("OrderType","0001");
        orderReserveMap.put("OrderValidTime",format.format(current));
        orderReserveMap.put("qrPattern","link");
        String orderReserve = JSON.toJSONString(orderReserveMap);

        Map<String, Object> submitFromData = new HashMap();
        submitFromData.put("Version", params.getPayVersion());
        submitFromData.put("AccessType","0");
        submitFromData.put("MerId",params.getMchId());
        submitFromData.put("MerOrderNo",bizRQ.getMchOrderNo());
        submitFromData.put("TranDate",dateFormat.format(new Date()));
        submitFromData.put("TranTime",timeFormat.format(new Date()));
        submitFromData.put("OrderAmt",String.valueOf(bizRQ.getAmount()));
        submitFromData.put("TranType","0009");
        submitFromData.put("BusiType","0001");
        submitFromData.put("CurryNo","CNY");

        submitFromData.put("MerPageUrl",bizRQ.getReturnUrl());
        submitFromData.put("MerBgUrl",getNotifyUrl());
        submitFromData.put("MerResv","productId");
        submitFromData.put("PayTimeOut","30");
        submitFromData.put("OrderReserved",orderReserve);
        submitFromData.put("CommodityMsg", bizRQ.getBody());//商品名称
        //String MerKeyFile = ResourceUtils.getFile("classpath:security.properties").getParentFile().getAbsolutePath();
//        String MerKeyFile = NetpayChinaPayController.class.getClassLoader().getResource("/").getPath();
//        SecssUtil secssUtil = new SecssUtil();
//        secssUtil.init(MerKeyFile);
//        System.out.println("weee"+secssUtil.getErrCode()+secssUtil.getErrMsg());
//        secssUtil.sign(submitFromData);
//        String signature = secssUtil.getSign();
//        submitFromData.put("Signature", signature);
//        String params = CommonUtil.getURLParam(submitFromData, true);
//        String httpRes =  HttpUtil.post("chinaPayConstants.PAY_SEND_URL", params);
//        //获取二维码链接
//        String codeUrl = "";
//        if ((httpRes == null) || (httpRes.getResponseCode() != 200)){
//            request.setAttribute("msg", "调用接口失败,响应码[" + Objects.requireNonNull(httpRes).getResponseCode() + "]");
//        }else {
//            String result = httpRes.getBody();
//            Map resultMap = CommonUtil.strToMap(result);
//            String respCode = (String)resultMap.get("respCode");
//            if ("0000".equals(respCode)){
//                secssUtil.verify(resultMap);
//                if("00".equals(secssUtil.getErrCode())){
//                    String payReserved = (String)resultMap.get("PayReserved");
//                    Map payReservedMap = JSON.parseObject(payReserved, Map.class);
//                    codeUrl = (String)payReservedMap.get("QrCode");
//                    codeUrl = URLDecoder.decode(codeUrl,"UTF-8");
//                }
//            }
//        }
//        if (!StringUtils.isNotBlank(codeUrl)) {
//            System.out.println("----生成二维码失败----");
//        } else {
//            //根据链接生成二维码
//            BufferedImage bufferedImage = QrCodeUtil.generate(qrCode, 300, 300);
//            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//            try {
//                ImageIO.write(bufferedImage, "png", outStream);
//                byte[] bytes = outStream.toByteArray();
//                imageBase64 = cn.hutool.core.codec.Base64.encode(bytes);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            request.setAttribute("imageBase64","data:image/png;base64,"+imageBase64);
//        }

        return res;
    }

}
