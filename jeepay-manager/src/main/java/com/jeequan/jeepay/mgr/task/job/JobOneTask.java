package com.jeequan.jeepay.mgr.task.job;

import org.springframework.stereotype.Component;


@Component("taskJobOne")
public class JobOneTask {
    public void taskWithParams(String params) {
        System.out.println("taskJobOne-执行有参示例任务：" + params);
    }

    public void taskNoParams() {
        System.out.println("taskJobOne-执行无参示例任务");
    }
}