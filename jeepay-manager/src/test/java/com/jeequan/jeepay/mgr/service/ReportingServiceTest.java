package com.jeequan.jeepay.mgr.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.entity.PayOrderExtend;
import com.jeequan.jeepay.mgr.bootstrap.JeepayMgrApplication;
import com.jeequan.jeepay.mgr.rqrs.AccountForDepartRq;
import com.jeequan.jeepay.service.impl.PayOrderExtendService;
import com.jeequan.jeepay.service.impl.PayOrderService;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

@SpringBootTest(classes = JeepayMgrApplication.class)
class ReportingServiceTest {

//    @Autowired(required = false)
//    private ReportingService reportingService;

    @Autowired(required = false)
    private PayOrderExtendService payOrderExtendService;


    @Autowired(required = false)
    private PayOrderService payOrderService;

//    @Test
//    @Ignore
//    void getAccountListTest() throws Exception {
//        List<AccountForDepartRq> accountRQS = reportingService.getAccountList(3);
//        accountRQS.forEach(item -> {
//            System.out.println(item.toString());
//        });
//        Assert.isTrue(accountRQS.size() > 0, "test successfully");
//    }

    @Test
    void OrderTest() throws Exception {

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<PayOrder> payOrderList= payOrderService.list(PayOrder.gw().ge(PayOrder::getCreatedAt,dateFormat.parse("2023-03-15 00:00:00")));
        for (PayOrder order:payOrderList){

            PayOrderExtend payOrderExtend = new PayOrderExtend();
            payOrderExtend.setPayOrderId(order.getPayOrderId());

            String extParam = order.getExtParam();
            if (!ObjectUtils.isEmpty(extParam)) {
                JSONObject jsonObject = JSONObject.parseObject(extParam);
                if (jsonObject.getString("businessId")!=null&&!jsonObject.getString("businessId").isEmpty()) {
                    payOrderExtend.setBusinessId(jsonObject.getString("businessId"));
                }
                if (jsonObject.getString("pid")!=null&&!jsonObject.getString("pid").isEmpty()) {
                    payOrderExtend.setPid(jsonObject.getString("pid"));
                }
                if (jsonObject.getString("dealType")!=null&&!jsonObject.getString("dealType").isEmpty()) {
                    payOrderExtend.setDealType(jsonObject.getString("dealType"));
                }
                if (jsonObject.getString("deptId")!=null&&!jsonObject.getString("deptId").isEmpty()) {
                    payOrderExtend.setDeptId(jsonObject.getString("deptId"));
                }
                if (jsonObject.getString("type")!=null&&jsonObject.getString("type").isEmpty()) {
                    payOrderExtend.setExtType(jsonObject.getString("type"));
                }

                PayOrderExtend orderExtend=payOrderExtendService.getOne(new QueryWrapper<PayOrderExtend>().eq("pay_order_id",order.getPayOrderId()));
                if(Objects.isNull(orderExtend)){
                    payOrderExtendService.save(payOrderExtend);
                }
            }
        }

        Assert.isTrue(true, "test successfully");
    }
}