package com.jeequan.jeepay.mgr.rqrs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Data
public class AccountForTenantRq {

    /**
     * 公司名称
     */
    private String groupName;


    /**
     * 集团总账
     */
    private double totalAccountForTenant;


    /**
     * 出账日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date AccountTime;


    /**
     * 汇总每个消费灯别
     */
    private Map<String, Double> cmpAccountDetailMap;


    /**
     * 部门账单详情
     */
    private List<AccountForDepartmentRq> accountForDepartmentRqs;

}
