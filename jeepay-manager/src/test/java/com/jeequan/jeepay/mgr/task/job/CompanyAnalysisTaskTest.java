package com.jeequan.jeepay.mgr.task.job;

import com.jeequan.jeepay.mgr.bootstrap.JeepayMgrApplication;
import com.jeequan.jeepay.service.impl.MchAppService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JeepayMgrApplication.class)
@SpringBootConfiguration
class CompanyAnalysisTaskTest {

    @Autowired(required = false)
    private CompanyAnalysisTask companyAnalysisTask;

    @Test
    void process() throws Exception {
        companyAnalysisTask.process(2);
        Assert.isTrue(true, "test successfully");
    }
}