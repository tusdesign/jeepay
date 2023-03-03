package com.jeequan.jeepay.mgr.rqrs;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class JobRQ {

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

}
