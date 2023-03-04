/*
 * Copyright (c) 2021-2031, 河北计全科技有限公司 (https://www.jeequan.com & jeequan@126.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jeequan.jeepay.mgr.aop;

import com.alibaba.fastjson.JSONObject;
import com.jeequan.jeepay.core.aop.Action;
import com.jeequan.jeepay.core.aop.MethodLog;
import com.jeequan.jeepay.core.beans.RequestKitBean;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.SysJobLog;
import com.jeequan.jeepay.core.entity.SysLog;
import com.jeequan.jeepay.core.exception.BizException;
import com.jeequan.jeepay.core.model.security.JeeUserDetails;
import com.jeequan.jeepay.service.impl.SysJobLogService;
import com.jeequan.jeepay.service.impl.SysLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 方法级日志切面组件
 *
 * @author chengzhengwen
 * @site https://www.jeequan.com
 * @date 2023-03-04 07:15
 */
@Component
@Aspect
public class JobLogAop {

    private static final Logger logger = LoggerFactory.getLogger(JobLogAop.class);

    @Autowired
    private SysJobLogService sysJobLogService;

    /**
     * 异步处理线程池
     */
    private final static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);

    /**
     * 切点
     */
    @Pointcut("@annotation(com.jeequan.jeepay.core.aop.Action)")
    public void methodJobPointcut() {
    }

    /**
     * 切面
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("methodJobPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        final SysJobLog jobLog = new SysJobLog();
        jobLog.setCreateTime(new Date());
        jobLog.setExectueStartTime(new Date());

        Object result = point.proceed();

        try {
            result=point.proceed();
            // 基础日志信息
            setJobLogInfo(point, jobLog);
            scheduledThreadPool.execute(() -> sysJobLogService.save(jobLog));
        } catch (Exception e) {
            jobLog.setRemark("methodLogError："+ e.getMessage());
            logger.error("methodLogError", e);
        }finally {
           // scheduledThreadPool.execute(() -> sysJobLogService.save(jobLog));
        }
        return result;
    }

    /**
     * @describe: 记录异常操作请求信息
     */
    @AfterThrowing(pointcut = "methodJobPointcut()", throwing = "e")
    public void doException(JoinPoint joinPoint, Throwable e) throws Exception {

        final SysJobLog jobLog = new SysJobLog();
        setJobLogInfo(joinPoint, jobLog);
        jobLog.setRemark(e instanceof BizException ? e.getMessage() : "请求异常");
        jobLog.setExectueResult("N");
        scheduledThreadPool.execute(() -> sysJobLogService.save(jobLog));
    }


    /**
     * @describe: job日志基本信息 公共方法
     */
    private void setJobLogInfo(JoinPoint joinPoint, SysJobLog jobLog) throws Exception {

        Signature sig = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        ReflectionUtils.makeAccessible(method);

        Object [] objects= joinPoint.getArgs();
        jobLog.setJobId(objects[1].toString());

        //        Field jobIdField = methodSignature.getClass().getDeclaredField("jobId");
        //        String jobId = String.valueOf(jobIdField.get(joinPoint.getThis()));
        //        jobLog.setJobId(jobId);
        //
        //        Field beanField = methodSignature.getClass().getDeclaredField("beanName");
        //        String beanName = String.valueOf(beanField.get(joinPoint.getThis()));

        Action methodCache = method.getAnnotation(Action.class);
        jobLog.setRemark(methodCache != null ? String.format("任务id:%s,任务名称：%s 执行成功",jobLog.getJobId(),methodCache.value()):"");
        jobLog.setExectueEndTime(new Date());
        jobLog.setExectueResult("Y");
    }

}
