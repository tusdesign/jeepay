package com.jeequan.jeepay.mgr.task;


import com.jeequan.jeepay.core.entity.SysJob;
import com.jeequan.jeepay.mgr.util.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

public class SchedulingRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingRunnable.class);

    private SysJob job;

    private String beanName;

    private String methodName;

    private String params;

    private String jobId;

    public SchedulingRunnable() {
    }

    public SchedulingRunnable(String beanName, String methodName) {
        this(beanName, methodName, null);
    }

    public SchedulingRunnable(String beanName, String methodName, String params) {
        this(beanName, methodName, params, UUID.randomUUID().toString());
        this.beanName = beanName;
        this.methodName = methodName;
        this.params = params;
        this.jobId = UUID.randomUUID().toString();
        this.job = null;

    }

    public SchedulingRunnable(String beanName, String methodName, String params, String jobId) {
        this.beanName = beanName;
        this.methodName = methodName;
        this.params = params;
        this.jobId = jobId;
        this.job = null;
    }

    public SchedulingRunnable(SysJob job) {
        this.beanName = job.getBeanName();
        this.methodName = job.getMethodName();
        this.params = job.getMethodParams();
        this.jobId = job.getJobId();
        this.job = job;
    }

    @Override
    public void run() {

        long startTime = System.currentTimeMillis();
        try {
            Object target = SpringContextUtils.getBean(beanName);

            Method method = null;
            if (StringUtils.isNotEmpty(params)) {
                method = target.getClass().getDeclaredMethod(methodName, SysJob.class);
            } else {
                method = target.getClass().getDeclaredMethod(methodName);
            }

            ReflectionUtils.makeAccessible(method);
            if (!Objects.isNull(this.job)) {
                method.invoke(target, this.job);
            } else {
                method.invoke(target);
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ex) {
            logger.error(String.format("定时任务执行异常 - bean：%s，方法：%s，参数：%s ", beanName, methodName, params), ex);
        } catch (Exception ex) {
            logger.error(String.format("定时任务执行异常 - bean：%s，方法：%s，参数：%s ", beanName, methodName, params), ex);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchedulingRunnable that = (SchedulingRunnable) o;
        if (params == null) {
            return beanName.equals(that.beanName) &&
                    methodName.equals(that.methodName) &&
                    that.params == null;
        }

        return beanName.equals(that.beanName) &&
                methodName.equals(that.methodName) &&
                params.equals(that.params);
    }

    @Override
    public int hashCode() {
        if (params == null) {
            return Objects.hash(beanName, methodName);
        }

        return Objects.hash(beanName, methodName, params);
    }
}