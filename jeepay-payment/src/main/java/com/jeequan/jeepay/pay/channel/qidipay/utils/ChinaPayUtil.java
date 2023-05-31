package com.jeequan.jeepay.pay.channel.qidipay.utils;


import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.jeequan.jeepay.core.model.params.qidipay.QidipayNormalMchParams;
import com.jeequan.jeepay.core.utils.SpringBeansUtil;
import com.jeequan.jeepay.pay.util.ChannelCertConfigKitBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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