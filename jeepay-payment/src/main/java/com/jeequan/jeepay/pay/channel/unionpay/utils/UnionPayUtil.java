package com.jeequan.jeepay.pay.channel.unionpay.utils;


import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.jeequan.jeepay.core.model.params.qidipay.QidipayNormalMchParams;
import com.jeequan.jeepay.core.model.params.unionpay.UnionPayNormalMchParams;
import com.jeequan.jeepay.core.utils.SpringBeansUtil;
import com.jeequan.jeepay.pay.util.ChannelCertConfigKitBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@Slf4j
@Component
public class UnionPayUtil {

    private SecssUtil secssUtil;

    public SecssUtil getSecssUtil() {
        return secssUtil;
    }

    public void setSecssUtil(SecssUtil secssUtil) {
        this.secssUtil = secssUtil;
    }


    public Boolean init(UnionPayNormalMchParams mchParams) {

        SecssUtil secssUtil = new SecssUtil();

        Properties properties = new Properties();

        ChannelCertConfigKitBean channelCertConfigKitBean = SpringBeansUtil.getBean(ChannelCertConfigKitBean.class);

        properties.setProperty(SecssConstants.SIGN_INVALID_FIELDS, "signature,CertId");
        properties.setProperty(SecssConstants.SIGNATURE_FIELD, "signature");
        if(StringUtils.isNotEmpty(mchParams.getSecret())) {
            properties.setProperty(SecssConstants.SECSS_PRIVATEALG, mchParams.getSecret());
        }
        if(StringUtils.isNotEmpty(mchParams.getChinaPayPrivateCert())) {
            properties.setProperty(SecssConstants.SECSS_PRIVATEPATH, channelCertConfigKitBean.getCertFilePath(mchParams.getChinaPayPrivateCert()));
        }
        if(StringUtils.isNotEmpty(mchParams.getPrivatePwd())){
            properties.setProperty(SecssConstants.SECSS_PRIVATEPWD, mchParams.getPrivatePwd());
        }
        if(StringUtils.isNotEmpty(mchParams.getPrivateKey())){
            properties.setProperty(SecssConstants.SECSS_PRIVATEKEY, mchParams.getPrivateKey());
        }else{
            properties.setProperty(SecssConstants.SECSS_PRIVATEKEY, "");
        }
        if(StringUtils.isNotEmpty(mchParams.getSecret())){
            properties.setProperty(SecssConstants.SECSS_PUBLICALG, mchParams.getSecret());
        }
        if(StringUtils.isNotEmpty(mchParams.getChinaPayPublicCert())){
            properties.setProperty(SecssConstants.SECSS_PUBLICPATH, channelCertConfigKitBean.getCertFilePath(mchParams.getChinaPayPublicCert()));
        }
        if(StringUtils.isNotEmpty(mchParams.getChinaPayPublicKey())){
            properties.setProperty(SecssConstants.SECSS_PUBLICKEY, mchParams.getChinaPayPublicKey());
        }else{
            properties.setProperty(SecssConstants.SECSS_PUBLICKEY, "");
        }
        properties.setProperty(SecssConstants.SECSS_EXCLUDEEXPIREDCERT, "true");

        secssUtil.init(properties);
        this.setSecssUtil(secssUtil);
        return true;
    }

    public Map<String, String> getResponseMap(String resp) {

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

    public String buildRequest(Map<String, Object> sParaTemp, String action, String strMethod, String strButtonName) {

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

    public static String getPayUrl(String payUrl) {
        if(StringUtils.isEmpty(payUrl)) {
            return payUrl;
        }
        if(!payUrl.endsWith("/")) {
            payUrl += "/";
        }
        return payUrl;
    }
}