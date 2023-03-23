package com.jeequan.jeepay.mgr.ctrl.anon;

import com.jeequan.jeepay.core.aop.MethodLog;
import com.jeequan.jeepay.core.constants.ApiCodeEnum;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.SysJob;
import com.jeequan.jeepay.core.exception.BizException;
import com.jeequan.jeepay.core.model.ApiRes;
import com.jeequan.jeepay.mgr.rqrs.EnumTime;
import com.jeequan.jeepay.mgr.rqrs.JobRQ;
import com.jeequan.jeepay.mgr.task.CronTaskRegistrar;
import com.jeequan.jeepay.mgr.task.SchedulingRunnable;
import com.jeequan.jeepay.service.impl.SysJobService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/anon/task")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    //企业账单job
    private final String COMPANYJOB = "companyAnalysisJob";

    //商户账单job
    private final String MERCHANTJOB = "merchatAnalysisJob";

    //testjob
    private final String TESTJOB = "orderJob";

    @Autowired
    private SysJobService sysJobService;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ApiRes startAtOnce(@RequestBody JobRQ job) throws BizException {

        if (Objects.isNull(job.getTaskType())) {
            return ApiRes.fail(ApiCodeEnum.PARAMS_ERROR, "任务类型不能为空");
        }
        if (StringUtils.isEmpty(job.getCronType())) {
            return ApiRes.fail(ApiCodeEnum.PARAMS_ERROR, "任务周期不能为空");
        }
        if (Arrays.asList(1, 2, 3).contains(job.getTaskType())) {
            return ApiRes.fail(ApiCodeEnum.PARAMS_ERROR, "任务类型不存在");
        }

        SysJob sysJob = new SysJob();
        sysJob.setJobId(job.getJobId());
        sysJob.setJobStatus(SysJob.NORMAL);
        sysJob.setMethodName("process");
        sysJob.setMethodParams(String.valueOf(EnumTime.TIMETYPE.get(job.getCronType()).key));
        sysJob.setTimeStart(job.getTimeStart());
        sysJob.setTimeEnd(job.getTimeEnd());

        if (job.getTaskType() == 1) sysJob.setBeanName(COMPANYJOB);
        if (job.getTaskType() == 2) sysJob.setBeanName(MERCHANTJOB);
        if (job.getTaskType() == 3) sysJob.setBeanName(TESTJOB);

        if (StringUtils.isEmpty(job.getCronExpression())) {
            sysJob.setCronExpression("0 0 * * * *");
        }else{
            sysJob.setCronExpression(job.getCronExpression());
        }

        if (!sysJobService.save(sysJob))
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_CREATE, "执行失败");
        else {
            if (sysJob.getJobStatus().equals(SysJob.NORMAL)) {
                SchedulingRunnable task = new SchedulingRunnable(sysJob) ;
                cronTaskRegistrar.addCronTask(task, sysJob.getCronExpression());
            }
        }
        return ApiRes.ok(job);
    }



}
