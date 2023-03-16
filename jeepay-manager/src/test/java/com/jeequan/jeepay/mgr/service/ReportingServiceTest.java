package com.jeequan.jeepay.mgr.service;

import com.jeequan.jeepay.core.entity.SysJob;
import com.jeequan.jeepay.mgr.bootstrap.JeepayMgrApplication;
import com.jeequan.jeepay.mgr.rqrs.AccountRQ;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JeepayMgrApplication.class)
@SpringBootConfiguration
class ReportingServiceTest {

    @Autowired(required = false)
    private ReportingService reportingService;

    @Test
    void getAccountListTest() throws Exception {
        List<AccountRQ> accountRQS = reportingService.getAccountList(3);
        accountRQS.forEach(item -> {
            System.out.println(item.toString());
        });
        Assert.isTrue(accountRQS.size() > 0, "test successfully");
    }

}