package com.jeequan.jeepay.mgr.ctrl.anon;

import com.jeequan.jeepay.core.aop.MethodLog;
import com.jeequan.jeepay.core.constants.ApiCodeEnum;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.SysJob;
import com.jeequan.jeepay.core.exception.BizException;
import com.jeequan.jeepay.core.model.ApiRes;
import com.jeequan.jeepay.mgr.bootstrap.TaskRunner;
import com.jeequan.jeepay.mgr.task.CronTaskRegistrar;
import com.jeequan.jeepay.mgr.task.SchedulingRunnable;
import com.jeequan.jeepay.service.impl.SysJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/anon/task")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    @Autowired
    private SysJobService sysJobService;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @MethodLog(remark = "添加任务")
    public ApiRes taskAdd(SysJob sysJob) throws BizException {
        boolean success = sysJobService.save(sysJob);
        if (!success)
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_CREATE, "新增失败");
        else {
            if (sysJob.getJobStatus().equals(SysJob.NORMAL)) {
                SchedulingRunnable task = new SchedulingRunnable(sysJob.getBeanName(), sysJob.getMethodName(), sysJob.getMethodParams());
                cronTaskRegistrar.addCronTask(task, sysJob.getCronExpression());
            }
        }
        return ApiRes.ok();
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    @MethodLog(remark = "修改任务")
    public ApiRes taskModify(SysJob sysJob) throws BizException {
        boolean success = sysJobService.updateById(sysJob);
        if (!success)
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_UPDATE, "修改失败");
        else {
            //先移除再添加
            if (sysJob.getJobStatus().equals(SysJob.NORMAL)) {
                SchedulingRunnable task = new SchedulingRunnable(sysJob.getBeanName(), sysJob.getMethodName(), sysJob.getMethodParams());
                cronTaskRegistrar.removeCronTask(task);
            }

            if (sysJob.getJobStatus().equals(SysJob.NORMAL)) {
                SchedulingRunnable task = new SchedulingRunnable(sysJob.getBeanName(), sysJob.getMethodName(), sysJob.getMethodParams());
                cronTaskRegistrar.addCronTask(task, sysJob.getCronExpression());
            }
        }
        return ApiRes.ok();
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @MethodLog(remark = "移除任务")
    public ApiRes taskDelete(SysJob sysJob) throws BizException {
        boolean success = sysJobService.removeById(sysJob.getJobId());
        if (!success)
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_DELETE, "删除失败");
        else {
            if (sysJob.getJobStatus().equals(SysJob.NORMAL)) {
                SchedulingRunnable task = new SchedulingRunnable(sysJob.getBeanName(), sysJob.getMethodName(), sysJob.getMethodParams());
                cronTaskRegistrar.removeCronTask(task);
            }
        }
        return ApiRes.ok();
    }


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @MethodLog(remark = "列出所有任务")
    public ApiRes taskList() throws BizException {
        List<SysJob> sysJobList = sysJobService.list();
        return ApiRes.ok(sysJobList);
    }


    @RequestMapping(value = "/operation", method = RequestMethod.POST)
    public ApiRes operation(SysJob existedSysJob) {
        SchedulingRunnable task = new SchedulingRunnable(existedSysJob.getBeanName(), existedSysJob.getMethodName(), existedSysJob.getMethodParams());
        if (existedSysJob.getJobStatus().equals(SysJob.NORMAL)) {
            cronTaskRegistrar.addCronTask(task, existedSysJob.getCronExpression());
        } else {
            cronTaskRegistrar.removeCronTask(task);
        }
        return ApiRes.ok();
    }


    @RequestMapping(value = "/once", method = RequestMethod.POST)
    @MethodLog(remark = "立即执行任务")
    public ApiRes startAtOnce(SysJob sysJob) throws BizException {
        sysJob.setCronExpression("0 0 * * * *");
        boolean success = sysJobService.save(sysJob);
        if (!success)
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_CREATE, "执行失败");
        else {
            if (sysJob.getJobStatus().equals(SysJob.NORMAL)) {
                SchedulingRunnable task = new SchedulingRunnable(sysJob.getBeanName(), sysJob.getMethodName(), sysJob.getMethodParams());
                cronTaskRegistrar.addCronTask(task, sysJob.getCronExpression());
            }
        }
        return ApiRes.ok();
    }

}
