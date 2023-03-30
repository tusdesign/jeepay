package com.jeequan.jeepay.mgr.service;

import com.jeequan.jeepay.mgr.bootstrap.JeepayMgrApplication;
import com.jeequan.jeepay.mgr.rqrs.AccountForDepartRq;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest(classes = JeepayMgrApplication.class)
class ReportingServiceTest {

    @Autowired(required = false)
    private ReportingService reportingService;

    @Test
    @Ignore
    void getAccountListTest() throws Exception {
        List<AccountForDepartRq> accountRQS = reportingService.getAccountList(3);
        accountRQS.forEach(item -> {
            System.out.println(item.toString());
        });
        Assert.isTrue(accountRQS.size() > 0, "test successfully");
    }

}