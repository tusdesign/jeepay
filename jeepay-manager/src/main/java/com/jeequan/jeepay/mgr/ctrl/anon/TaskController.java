package com.jeequan.jeepay.mgr.ctrl.anon;

import com.jeequan.jeepay.core.aop.MethodLog;
import com.jeequan.jeepay.core.constants.ApiCodeEnum;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.SysJob;
import com.jeequan.jeepay.core.exception.BizException;
import com.jeequan.jeepay.core.model.ApiRes;
import com.jeequan.jeepay.mgr.bootstrap.TaskRunner;
import com.jeequan.jeepay.mgr.rqrs.EnumTime;
import com.jeequan.jeepay.mgr.rqrs.JobRQ;
import com.jeequan.jeepay.mgr.task.CronTaskRegistrar;
import com.jeequan.jeepay.mgr.task.SchedulingRunnable;
import com.jeequan.jeepay.service.impl.SysJobService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/anon/task")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    @Autowired
    private SysJobService sysJobService;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ApiRes startAtOnce(JobRQ job) throws BizException {

        if (Objects.isNull(job.getTaskType())) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_CREATE, "任务类型不能为空");
        }
        if (StringUtils.isEmpty(job.getCronType())) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_CREATE, "任务周期不能为空");
        }
        SysJob sysJob = new SysJob();
        sysJob.setJobId(job.getJobId());
        sysJob.setJobStatus(SysJob.NORMAL);
        sysJob.setMethodName("process");
        sysJob.setMethodParams(String.valueOf(EnumTime.TIMETYPE.get(job.getCronType())));

        if (job.getTaskType() == 1) {
            sysJob.setBeanName("companyAnalysisTask");
        } else if (job.getTaskType() == 2) {
            sysJob.setBeanName("merchatAnalysisTask");
        }
        if (StringUtils.isEmpty(job.getCronExpression())) {
            sysJob.setCronExpression("0 0 * * * *");
        }
        boolean success = sysJobService.save(sysJob);
        if (!success)
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_CREATE, "执行失败");
        else {
            if (sysJob.getJobStatus().equals(SysJob.NORMAL)) {
                SchedulingRunnable task = new SchedulingRunnable(sysJob.getBeanName(), sysJob.getMethodName(), sysJob.getMethodParams());
                cronTaskRegistrar.addCronTask(task, sysJob.getCronExpression());
            }
        }
        return ApiRes.ok(job);
    }
}
