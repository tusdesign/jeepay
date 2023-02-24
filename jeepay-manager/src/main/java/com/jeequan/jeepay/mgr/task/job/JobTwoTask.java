package com.jeequan.jeepay.mgr.task.job;

import org.springframework.stereotype.Component;


@Component("taskJobTwo")
public class JobTwoTask {
    public void taskWithParams(String params) {
        System.out.println("taskJobTwo-执行有参示例任务：" + params);
    }

    public void taskNoParams() {
        System.out.println("taskJobTwo-执行无参示例任务");
    }
}