package com.jeequan.jeepay.pay.channel.qidipay.utils;


import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.jeequan.jeepay.core.model.params.qidipay.QidipayNormalMchParams;
import com.jeequan.jeepay.core.utils.SpringBeansUtil;
import com.jeequan.jeepay.pay.util.ChannelCertConfigKitBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@Slf4j
public class ChinaPayUtil {

    public static SecssUtil init(QidipayNormalMchParams mchParams) {

        SecssUtil secssUtil = new SecssUtil();

        Properties properties = new Properties();

        ChannelCertConfigKitBean channelCertConfigKitBean = SpringBeansUtil.getBean(ChannelCertConfigKitBean.class);

        properties.setProperty(SecssConstants.SIGN_INVALID_FIELDS, "signature,CertId");
        properties.setProperty(SecssConstants.SIGNATURE_FIELD, "signature");
        properties.setProperty(SecssConstants.SECSS_PRIVATEALG, mchParams.getSecret());
        properties.setProperty(SecssConstants.SECSS_PRIVATEPATH, channelCertConfigKitBean.getCertFilePath(mchParams.getChinaPayPrivateCert()));
        properties.setProperty(SecssConstants.SECSS_PRIVATEPWD, mchParams.getPrivatePwd());
        properties.setProperty(SecssConstants.SECSS_PRIVATEKEY, mchParams.getPrivateKey());
        properties.setProperty(SecssConstants.SECSS_PUBLICALG, mchParams.getSecret());
        properties.setProperty(SecssConstants.SECSS_PUBLICPATH, channelCertConfigKitBean.getCertFilePath(mchParams.getChinaPayPublicCert()));
        properties.setProperty(SecssConstants.SECSS_PUBLICKEY, mchParams.getChinaPayPublicKey());
        properties.setProperty(SecssConstants.SECSS_EXCLUDEEXPIREDCERT, "true");

        boolean bool = secssUtil.init(properties);
        if (bool) {
            log.info("ChinaPay交易证书、验签证书初始化成功！");
        } else {
            log.error("ChinaPay交易证书、验签证书初始化失败：" + secssUtil.getErrCode() + "=" + secssUtil.getErrMsg());
        }
        return secssUtil;
    }

    public static boolean verifyNotify(Map<String, String> notifyMap, QidipayNormalMchParams normalMchParams) {

        SecssUtil secssUtil = ChinaPayUtil.init(normalMchParams);
        try {
            //验签
            String sign = notifyMap.get("Signature");
            if (StringUtils.isNotEmpty(sign)) {
                //入参：返回商户报文中的所有参数
                secssUtil.verify(notifyMap);
            }
            if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {
                System.out.println(secssUtil.getErrCode() + "=" + secssUtil.getErrMsg());
                System.out.println("ChinaPay返回的应答数据【验签】失败:" + secssUtil.getErrMsg());
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Map<String,String> getResponseMap(String resp) {

        String[] strs = resp.split("&", -1);
        Map<String, String> resultMap = new TreeMap<String, String>();
        for (String str : strs) {
            String[] keyValues = str.split("=", -1);
            if (keyValues.length < 2) {
                continue;
            }
            String key = keyValues[0];
            String value = keyValues[1];
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            //响应字段解码
            try {
                value = URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            resultMap.put(key, value);
        }
        return resultMap;
    }

    public static String buildRequest(Map<String, Object> sParaTemp, String action, String strMethod, String strButtonName) {

        List<String> keys = new ArrayList<String>(sParaTemp.keySet());
        StringBuffer sbHtml = new StringBuffer();

        sbHtml.append("<form id=\"rppaysubmit\" name=\"rppaysubmit\" action=\"" + action + "\" method=\"" + strMethod
                + "\">");

        for (int i = 0; i < keys.size(); i++) {

            String name = (String) keys.get(i);
            Object object = sParaTemp.get(name);
            String value = "";

            if (object != null) {
                value = String.valueOf(sParaTemp.get(name));
            }
            sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
        }
        sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
        return sbHtml.toString();
    }

}