package com.jeequan.jeepay.mgr.rqrs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Map;


@Data
public class AccountForDepartmentRq {

    /**
     * 消费类别：餐饮、停车等
     */
    private String appName;

    /**
     * 每个消费类别的账目汇总
     */
    private double totalAccountForApp;

    /**
     * 出账日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date AccountTime;

    /**
     * 汇总每个部门消费情况
     */
    private Map<String, Double> orgAccountDetailMap;

}