//package com.jeequan.jeepay.mgr.task.job;
//
//import com.jeequan.jeepay.mgr.bootstrap.JeepayMgrApplication;
//import jdk.nashorn.internal.ir.annotations.Ignore;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringBootConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.util.Assert;
//
//@SpringBootTest(classes = JeepayMgrApplication.class)
//@SpringBootConfiguration
//class CompanyAnalysisTaskTest {
//
//    @Autowired(required = false)
//    private CompanyAnalysisJob companyAnalysisTask;
//
//    @Test
//    @Ignore
//    void process() throws Exception {
//        companyAnalysisTask.process("month");
//        Assert.isTrue(true, "test successfully");
//    }
//}