package com.jeequan.jeepay.mgr.task.job;

import cn.hutool.core.date.DateTime;
import com.jeequan.jeepay.core.entity.SysJob;
import com.jeequan.jeepay.mgr.bootstrap.JeepayMgrApplication;
import com.jeequan.jeepay.mgr.rqrs.EnumTime;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest(classes = JeepayMgrApplication.class)
@SpringBootConfiguration
class CompanyAnalysisTaskTest {

    @Autowired(required = false)
    private CompanyAnalysisJob companyAnalysisTask;

    @Test
    @Ignore
    void process() throws Exception {

        SysJob sysJob = new SysJob();
        sysJob.setJobId(sysJob.getJobId());
        sysJob.setJobStatus(SysJob.NORMAL);
        sysJob.setMethodName("process");
        sysJob.setMethodParams("month");
        sysJob.setTimeStart(DateTime.now());
        sysJob.setTimeEnd(DateTime.now());
        sysJob.setBeanName("companyAnalysisJob");
        sysJob.setCronExpression("0 0 * * * *");
        companyAnalysisTask.process(sysJob);
        Assert.isTrue(true, "test successfully");
    }
}