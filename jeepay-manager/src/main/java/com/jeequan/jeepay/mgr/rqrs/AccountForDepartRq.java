package com.jeequan.jeepay.mgr.rqrs;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AccountForDepartRq {

    @Data
    public static class DepartMentAccountRQ {

        private String deptName;//部门
        private String levelName;//部门等级，是集团还是子部门
        private double totalAccountForDept;//部门总账
        private Map<String, Double> accountDetail;//消费类型: 餐饮、停车等
    }

    private String companyName;//公司名称
    private double totalAccountForCompany;//集团总账
    private List<DepartMentAccountRQ> departMentAccountRQList;//部门消费明细

}






