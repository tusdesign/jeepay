package com.jeequan.jeepay.pay.channel.qidipay.utils;


import com.chinapay.secss.SecssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ChinaPayUtil {

    public static final SecssUtil secssUtil;

    static {
        secssUtil = new SecssUtil();
        Resource resource = (Resource) new ClassPathResource("./security.properties");
        File file = null;
        try {
            file = resource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean bool = secssUtil.init(file.getPath());
        if (bool) {

            log.info("ChinaPay交易证书、验签证书初始化成功！");
        } else {
            log.error("ChinaPay交易证书、验签证书初始化失败：" + secssUtil.getErrCode() + "=" + secssUtil.getErrMsg());
        }
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