package com.jeequan.jeepay.mgr.bootstrap;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jeequan.jeepay.core.entity.MchNotifyRecord;
import com.jeequan.jeepay.core.entity.SysJob;
import com.jeequan.jeepay.mgr.task.CronTaskRegistrar;
import com.jeequan.jeepay.mgr.task.SchedulingRunnable;
import com.jeequan.jeepay.service.impl.SysJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    @Autowired
    private SysJobService sysJobService;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @Override
    public void run(String... args) throws Exception {

        // 初始加载数据库里状态为正常的定时任务
        LambdaQueryWrapper<SysJob> wrapper = SysJob.gw();
        List<SysJob> jobList = sysJobService.list(wrapper.eq(SysJob::getJobStatus, SysJob.NORMAL));

        if (CollectionUtils.isNotEmpty(jobList)) {
            for (SysJob job : jobList) {
                SchedulingRunnable task = new SchedulingRunnable(job.getBeanName(), job.getMethodName(), job.getMethodParams());
                cronTaskRegistrar.addCronTask(task, job.getCronExpression());
            }
            logger.info("定时任务已加载完毕...");
        }
    }
}
