package com.jeequan.jeepay.mgr.rqrs;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.UUID;

@Data
public class TaskScheduleRq {

    /**
     * jobId
     */
    @JsonIgnore
    private final String jobId= UUID.randomUUID().toString();

    /**
     * 创建时间
     */
    @JsonIgnore
    private final Date createTime=new Date();

    /**
     * 更新时间
     */
    @JsonIgnore
    private final Date updateTime=new Date();

    /**
     * 任务类型
     */
    private byte taskType;

    /**
     * 任务周期
     */
    private String cronType ;

    /**
     * cron表达式
     */
    private String cronExpression;

    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timeStart;

    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timeEnd;

}
