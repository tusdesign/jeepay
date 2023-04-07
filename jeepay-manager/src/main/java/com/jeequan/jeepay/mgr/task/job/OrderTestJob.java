package com.jeequan.jeepay.mgr.task.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jeequan.jeepay.core.entity.MchApp;
import com.jeequan.jeepay.core.entity.SysJob;
import com.jeequan.jeepay.mgr.task.SchedulingRunnable;
import com.jeequan.jeepay.mgr.util.EncryptHelper;
import com.jeequan.jeepay.util.JeepayKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

@Component("orderJob")
public class OrderTestJob extends AbstractAnalysisJob {

    private static final Logger logger = LoggerFactory.getLogger(OrderTestJob.class);

    private final String mchNo = "M1672977768";
    private final String url = "http://10.10.10.10:9216/api/pay/unifiedOrder";

    private final String[] departments = new String[]{
            "ee211e16-a63e-46c2-a85c-3fcab4493751"
            , "b874f790-3f55-43ce-900a-5bebddae94d8"
            , "55770282-8b40-41b0-aa8d-de49b1317853"
            , "08c1a46d-db52-4556-a854-f3bc7ab836ca"
            ,"14cf0ef4-188a-48f8-a982-64b017154408"
            ,"c34e8885-fbac-4ccd-9554-c3291ae456f0"
            , "57f5bd5a-3de4-466f-8a0e-b27e16a31657"
            ,"570d4681-591c-403a-925a-637c9292ee10"
            ,"77fb34ae-2070-4666-8c02-a1809d5fca47"
            ,"17fd308f-6e58-4c8c-8756-2a34cfd0ab96"
            ,"a3127730-5943-4e5a-9447-ef77af9ed286"};

    @Autowired
    private EncryptHelper encryptHelper;

    @Resource(name = "customRestTemplate")
    private RestTemplate restTemplate;

    @Override
    public void process(SysJob job) throws Exception {

        //应用列表
        Map<String, String> appMap = new HashMap<String, String>() {{

            //餐饮
            put("6401ba8fe4b0d1d19a948fb2", "ToyBZhcf08pXTJXOFQsOlfc5z2DajOlgn2n49xA4Kr5ql2nbQiR15rJhbiVXnb2hngTl12MIn6YfimbkPWZ37DBeSKC1zd3i8ivgIa4gXThOrsBqMi2Qiai2673WjzBp");
            //停车
            put("63e601b9e4b05255970d6f37", "BD7Hdbxbw8meSyPUpxwkLo1pEMjT8QggpNJwLnDTTETB6NhWzGxYk5NOEXx5tVt1siHJ2DRoxmUNIw4NOkLuCgaPPXfoUtN8B1b3qPdU3hlj7qmsJP2DBqKA8gJQUHXm");
            //会议
            put("63b79db7e4b05255970d6f36", "vMbwmEnzG9ed8GffojvxlzMfhHlnZzolRcUbJcfUEXUpJkqElV5UG4y2Qn9tQxAnjbr7C9NbWw0RkAYhsWfAgJsC83v1t8SSjtIxHQD7dfFggoPjOB8gqlRPKuKvgkWq");
            //用电
            put("63b79d68e4b05255970d6f35", "1hk6w5tbtqfc9g4x4iv5ju1vdtl6gbjb48q0p0pimz0c7hgw9ie5bqnsywzramkvcl9b9eaeg8ptzu2msgwx7x5z0h16odrs33ycxqjgd0jxd7z58z4686shobeh490a");

        }};
        for (Map.Entry<String, String> entry : appMap.entrySet()) {

            for (int i = 0; i < 100; i++) {

                HttpHeaders headers = new HttpHeaders();
                headers.add("X-API-KEY", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3R1eG0uYXJ0IiwidXNlcm5hbWUiOiJ0dXNkZXNpZ24ifQ.0SCNQBLp6_e3EEcQcT0Rqwp1QJ4AonY5eoOTiKMIkk0");
                headers.setContentType(MediaType.APPLICATION_JSON);

                //提交参数设置
                Map<String, Object> signMap = new HashMap<String, Object>();

                signMap.put("appId", entry.getKey());
                signMap.put("mchNo", mchNo);
                signMap.put("wayCode", "QIDI_APP");
                signMap.put("currency", "cny");
                signMap.put("subject", "商品标题测试");
                signMap.put("body", "商品描述测试");

                String extParamTemplate = getExtra(entry.getKey(), departments.length);

                signMap.put("extParam", extParamTemplate);
                signMap.put("mchOrderNo", "mho" + new Date().getTime());
                signMap.put("amount", String.valueOf((int) (Math.random() * 1000 - 1)));
                signMap.put("clientIp", "192.168.0.1");

                int requestTime = (int) (System.currentTimeMillis() / 1000);
                signMap.put("reqTime", Integer.toString(requestTime));
                signMap.put("version", "1.0");
                signMap.put("signType", "MD5");


                String signature = encryptHelper.getSign(signMap, entry.getValue());
                if (signature != null) {
                    signMap.put("sign", signature);
                }

                // 组装请求体
                JSONObject object = new JSONObject(signMap);
                HttpEntity<String> request = new HttpEntity<String>(object.toString(), headers);

                // 发送post请求，并打印结果，以String类型接收响应结果JSON字符串
                String result = restTemplate.postForObject(url, request, String.class);
                System.out.println(result);
            }

            logger.info("完成应用{}的数据填充，数据一百条",entry.getKey());
        }
    }


    private String getExtra(String appId, int size) {
        int rands = new Random().nextInt(size);
        Map<String, String> map = new HashMap<String, String>() {
            {
                put("businessId", "p" + IdWorker.getIdStr());
                put("deptId", departments[rands]);
                put("dealType", "DEPARTMENTAL");
                put("pid", "m" + IdWorker.getIdStr());
            }
        };

        if(appId=="63e601b9e4b05255970d6f37"){
            map.put("businessId", CarNumGenerator.getCarNum()); //businessId更改为车牌
        }

        if(appId=="63b79d68e4b05255970d6f35"){
            map.put("type", UUID.randomUUID().toString().split("-")[1]);//电费增加type
        }
        return JSONObject.toJSONString(map);
    }


    // 随机生成车牌号
    private static class CarNumGenerator {

        /**
         * 中国各个地区的数组
         */
        private static final String[] CAR_AREA_ARRAY = {"京", "津", "沪", "渝", "冀", "豫", "云", "辽", "黑",
                "湘", "皖", "鲁", "新", "苏", "浙", "赣", "鄂", "桂", "甘", "晋", "蒙", "陕", "吉", "闽", "贵", "粤", "青", "藏",
                "川", "宁", "琼"};

        /**
         * 城市代码，不能有字母 I 和 O
         */
        private static final String[] CAR_LETTER_ARRAY = {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K",
                "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V"};

        /**
         * 车牌号，不能有字母 I 和 O
         */
        private static final String[] CAR_CHAR_ARRAY = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K",
                "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        /**
         * 车牌编号长度
         */
        private static final int CAR_NUM_LENGTH = 5;

        public static String getCarNum() {
            // 随机获取地区
            String area =  CAR_AREA_ARRAY[(int)(Math.random() * CAR_AREA_ARRAY.length)];

            // 随机生成城市编号
            String cityCode = CAR_LETTER_ARRAY[(int)(Math.random() * CAR_LETTER_ARRAY.length)];

            // 循环5次，生成车牌号
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<CAR_NUM_LENGTH; i++) {
                sb.append(CAR_CHAR_ARRAY[(int)(Math.random()*CAR_CHAR_ARRAY.length)]);
            }

            // 拼接
            return area + cityCode + sb;
        }

        public static void main(String[] args) {
            String carNum = getCarNum();
            System.out.println(carNum);
        }
    }

}
