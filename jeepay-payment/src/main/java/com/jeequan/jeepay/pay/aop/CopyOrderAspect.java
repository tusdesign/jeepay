package com.jeequan.jeepay.pay.aop;

import com.alibaba.fastjson.JSONObject;
import com.jeequan.jeepay.core.aop.Action;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.entity.PayOrderExtend;
import com.jeequan.jeepay.service.impl.PayOrderExtendService;
import com.jeequan.jeepay.service.impl.PayOrderService;
import com.jeequan.jeepay.service.impl.SysLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
@Aspect
public class CopyOrderAspect {

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private PayOrderExtendService payOrderExtendService;

    private static final Logger logger = LoggerFactory.getLogger(CopyOrderAspect.class);

    private final static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);


    @Pointcut("@annotation(com.jeequan.jeepay.core.aop.Action)")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void doBeforeController(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Action action = method.getAnnotation(Action.class);
        System.out.println("action名称 " + action.value());
    }

    // 返回后通知：在调用目标方法结束后执行【出现异常，不执行】
    @AfterReturning(value = "pointCut()", returning = "retValue")
    public void afterReturning(JoinPoint jp, Object retValue) throws Throwable {
        Object[] args = jp.getArgs();
        System.out.println("Aop.afterReturning() 目标方法+" + jp.getSignature().getName() + "返回值:" + retValue);

        PayOrder payOrder = (PayOrder)args[0];
        scheduledThreadPool.execute(() -> savePayOrderExtend(payOrder));
    }

    private boolean savePayOrderExtend(PayOrder order) {

        PayOrderExtend payOrderExtend = new PayOrderExtend();
        payOrderExtend.setPayOrderId(order.getPayOrderId());

        String extParam = order.getExtParam();
        if (!ObjectUtils.isEmpty(extParam)) {
            JSONObject jsonObject = JSONObject.parseObject(extParam);
            if (!jsonObject.getString("businessId").isEmpty()) {
                payOrderExtend.setBusinessId(jsonObject.getString("businessId"));
            }
            if (!jsonObject.getString("pid").isEmpty()) {
                payOrderExtend.setPid(jsonObject.getString("pid"));
            }
            if (!jsonObject.getString("dealType").isEmpty()) {
                payOrderExtend.setDealType(jsonObject.getString("dealType"));
            }
            if (!jsonObject.getString("deptId").isEmpty()) {
                payOrderExtend.setDeptId(jsonObject.getString("deptId"));
            }
            return payOrderExtendService.save(payOrderExtend);
        }
       return false;
    }

    @Around(value = "pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("环绕开始...");

        Object[] args = pjp.getArgs();
        Object retValue = pjp.proceed();// 执行目标方法
        System.out.println("环绕结束...");
        return retValue;
    }


    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void doException(JoinPoint joinPoint, Throwable e) throws Exception {
        // final SysLog sysLog = new SysLog();
        // // 基础日志信息
        // sysLog.setOptResInfo(e instanceof BizException ? e.getMessage() : "请求异常");
        // scheduledThreadPool.execute(() -> sysLogService.save(sysLog));
        logger.error("CopyOrderAspect error", e);
    }
}
