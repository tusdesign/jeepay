package com.jeequan.jeepay.mgr.service;


import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jeequan.jeepay.core.entity.OrderStatisticsCompany;
import com.jeequan.jeepay.core.entity.OrderStatisticsDept;
import com.jeequan.jeepay.mgr.rqrs.AccountRQ;
import com.jeequan.jeepay.service.impl.OrderStatisticsCompanyService;
import com.jeequan.jeepay.service.impl.OrderStatisticsDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportingService {

    @Autowired
    private OrderStatisticsDeptService statisticsDeptService;

    /**
     * 根据月份查找到账单
     * @param month 月份
     * @return List<AccountRQ>
     */
    public List<AccountRQ> getAccountList(int month,long currentTime){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Date date = new Date(currentTime);
        String timeRange= formatter.format(date);

        QueryWrapper<OrderStatisticsDept> queryWrapper=new QueryWrapper<>();
        queryWrapper.ge(currentTime>0,"created_at",timeRange);
        queryWrapper.eq(month>0,"MONTH(FROM_UNIXTIME(analyse_id/1000))",month);

        List<OrderStatisticsDept> orderStatisticsDeptList= statisticsDeptService.list(queryWrapper);

        Map<String, List<OrderStatisticsDept>> map = orderStatisticsDeptList.stream()
                .sorted(Comparator.comparing(OrderStatisticsDept::getAnalyseId))
                .collect(Collectors.groupingBy(OrderStatisticsDept::getParentName));

        return new ArrayList<>();
    }

}
