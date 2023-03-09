package com.jeequan.jeepay.pay;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jeequan.jeepay.core.entity.MchApp;
import com.jeequan.jeepay.pay.bootstrap.JeepayPayApplication;
import com.jeequan.jeepay.service.impl.MchAppService;
import com.jeequan.jeepay.util.JeepayKit;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;

//@MapperScan("com.jeequan.jeepay.service.mapper")    //Mybatis mapper接口路径
//@ComponentScan(basePackages = "com.jeequan.jeepay.*")
//@Configuration
@SpringBootTest(classes = JeepayPayApplication.class)
@SpringBootConfiguration
public class initPayOrderTest {

    final static Logger _log = LoggerFactory.getLogger(initPayOrderTest.class);

    @Autowired(required = false)
    private MchAppService mchAppService;


    @Test
    public void testRand(){

        String [] detps=new String[]{
                "ee211e16-a63e-46c2-a85c-3fcab4493751"
                ,"b874f790-3f55-43ce-900a-5bebddae94d8"
                ,"55770282-8b40-41b0-aa8d-de49b1317853"
                ,"08c1a46d-db52-4556-a854-f3bc7ab836ca"};

        //int rands=new Random().nextInt(4);
        for(int i=0;i<100;i++){
            int rands=new Random().nextInt(4);
            System.out.println(detps[rands]);
        }
    }

    @Test
    public void testPayApi() {

        // 请求地址
        String url = "http://10.10.10.10:9216/api/pay/unifiedOrder";
        String API_SIGN_NAME = "sign";
        RestTemplate restTemplate = new RestTemplate();
        String [] apps=new String[]{"63b79db7e4b05255970d6f36","63e601b9e4b05255970d6f37"};
        String [] detps=new String[]{
                "ee211e16-a63e-46c2-a85c-3fcab4493751"
                ,"b874f790-3f55-43ce-900a-5bebddae94d8"
                ,"55770282-8b40-41b0-aa8d-de49b1317853"
                ,"08c1a46d-db52-4556-a854-f3bc7ab836ca"};

        List<MchApp> mchAppList = mchAppService.list(MchApp.gw().in(MchApp::getAppId,apps));
        mchAppList.forEach(item -> {

            String appId = item.getAppId();
            String mchNo = item.getMchNo();
            String apiKey = item.getAppSecret();

            for (int i = 0; i < 50000; i++) {

                int rands=new Random().nextInt(4);
                HttpHeaders headers = new HttpHeaders();
                headers.add("X-API-KEY", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3R1eG0uYXJ0IiwidXNlcm5hbWUiOiJ0dXNkZXNpZ24ifQ.0SCNQBLp6_e3EEcQcT0Rqwp1QJ4AonY5eoOTiKMIkk0");
                headers.setContentType(MediaType.APPLICATION_JSON);

                //提交参数设置
                Map<String, Object> signMap = new HashMap<String, Object>();

                signMap.put("appId", appId);
                signMap.put("mchNo", mchNo);
                signMap.put("wayCode", "QIDI_APP");
                signMap.put("currency", "cny");
                signMap.put("subject", "商品标题");
                signMap.put("body", "商品描述");

                Map<String,String> map=new HashMap<String,String>(){
                    {
                        put("businessId","p" + IdWorker.getIdStr());
                        put("deptId", detps[rands]);
                        put("dealType","DEPARTMENTAL");
                        put("pid", "m" + IdWorker.getIdStr());
                    }
                };
                String extParamTemplate= JSONObject.toJSONString(map);
                signMap.put("extParam", extParamTemplate);
                signMap.put("mchOrderNo", "mho" + new Date().getTime());
                signMap.put("amount", String.valueOf((int)(Math.random()*1000-1)));
                signMap.put("clientIp", "192.168.0.1");

                int requestTime = (int) (System.currentTimeMillis() / 1000);
                signMap.put("reqTime", Integer.toString(requestTime));
                signMap.put("version", "1.0");
                signMap.put("signType", "MD5");


                String signature = JeepayKit.getSign(signMap, apiKey);
                String signature2 = getSign(signMap, apiKey);
                if (signature.equals(signature2))
                    System.out.println("true");
                if (signature != null) {
                    signMap.put(API_SIGN_NAME, signature);
                }

                // 组装请求体
                JSONObject object = new JSONObject(signMap);
                HttpEntity<String> request =
                        new HttpEntity<String>(object.toString(), headers);

                // 发送post请求，并打印结果，以String类型接收响应结果JSON字符串
                String result = restTemplate.postForObject(url, request, String.class);
                System.out.println(result);

            }

        });
        Assert.isTrue(true, "test successfully");
    }


    /**
     * <p><b>Description: </b>获取签名
     * <p>2023年2月8日 上午11:32:46
     *
     * @param map    参数Map
     * @param apiKey 商户秘钥
     * @return
     */
    public String getSign(Map<String, Object> map, String apiKey) {
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (null != entry.getValue() && !"".equals(entry.getValue())) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString() + "key=" + apiKey;
        result = md5(result, "UTF-8").toUpperCase();
        return result;
    }

    private String md5(String value, String charset) {
        MessageDigest md = null;
        try {
            byte[] data = value.getBytes(charset);
            md = MessageDigest.getInstance("MD5");
            byte[] digestData = md.digest(data);
            return toHex(digestData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String toHex(byte input[]) {
        if (input == null)
            return null;
        StringBuffer output = new StringBuffer(input.length * 2);
        for (int i = 0; i < input.length; i++) {
            int current = input[i] & 0xff;
            if (current < 16)
                output.append("0");
            output.append(Integer.toString(current, 16));
        }
        return output.toString();
    }

}
