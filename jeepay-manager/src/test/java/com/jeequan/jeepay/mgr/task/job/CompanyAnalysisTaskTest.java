package com.jeequan.jeepay.mgr.task.job;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import com.jeequan.jeepay.core.constants.ApiCodeEnum;
import com.jeequan.jeepay.core.entity.SysJob;
import com.jeequan.jeepay.core.model.ApiRes;
import com.jeequan.jeepay.mgr.bootstrap.JeepayMgrApplication;
import com.jeequan.jeepay.mgr.util.EnumTime;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Objects;

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

        JSONObject object = new JSONObject();
        object.put("period", "month");
        object.put("timeStart", sysJob.getTimeStart());
        object.put("timeEnd", sysJob.getTimeEnd());
        sysJob.setMethodParams(JSONObject.toJSONString(object));

        sysJob.setBeanName("companyAnalysisJob");
        sysJob.setCronExpression("0 0 * * * *");
        companyAnalysisTask.process(sysJob);
        Assert.isTrue(true, "test successfully");
    }

    @Test
    @Ignore
    void process2() throws Exception {

        try {

            String str1="fsdkfjks$fkdjfkjjk";
            String [] strs_1=str1.split("\\$");
            for (String item:strs_1){
                System.out.println(item);
            }

            String str2="fsdkfjks$";
            String [] strs_2=str2.split("\\$");
            for (String item:strs_2){
                System.out.println(item);
            }


            String str3="fsdkfjks";
            String [] strs_3=str3.split("\\$");
            for (String item:strs_3){
                System.out.println(item);
            }

//            MutablePair<String, String> a=MutablePair.of("","");
//            String b=String.valueOf(a.left.hashCode());
//            System.out.println(b);
        }catch (Exception e){
            e.printStackTrace();
        }

        MutablePair<String, String> a= companyAnalysisTask.getDept("letemporaryorg");
        Assert.isTrue(true, "test successfully");
    }
}