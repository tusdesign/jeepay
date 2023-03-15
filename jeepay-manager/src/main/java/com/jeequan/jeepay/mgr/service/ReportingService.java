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
     *
     * @param month 月份
     * @return List<AccountRQ>
     */
    public List<AccountRQ> getAccountList(int month, long currentTime) {

        List<AccountRQ> accountRQList = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Date date = new Date(currentTime);
        String timeRange = formatter.format(date);

        QueryWrapper<OrderStatisticsDept> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(currentTime > 0, "created_at", timeRange);
        queryWrapper.eq(month > 0, "MONTH(FROM_UNIXTIME(analyse_id/1000))", month);
        List<OrderStatisticsDept> orderStatisticsDeptList = statisticsDeptService.list(queryWrapper);

        List<OrderStatisticsDept> distinctStatisticsDeptList = orderStatisticsDeptList.stream()
                .sorted(Comparator.comparing(OrderStatisticsDept::getAnalyseId).reversed())
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(OrderStatisticsDept::getAnalyseId))), ArrayList::new));

        Map<String, List<OrderStatisticsDept>> companyMap = distinctStatisticsDeptList.stream()
                .collect(Collectors.groupingBy(OrderStatisticsDept::getParentName));

        companyMap.entrySet().forEach(entry -> {
                    AccountRQ accountRQ = new AccountRQ();
                    accountRQ.setCompanyName(entry.getKey());
                    accountRQ.setTotalAccountForCompany(entry.getValue().stream().mapToDouble(OrderStatisticsDept::getAmount).sum());
                    accountRQ.setDepartMentAccountRQList(new ArrayList<AccountRQ.DepartMentAccountRQ>());


                    Map<OrderStatisticsDept, Double> map1 = entry.getValue().stream().collect(Collectors.groupingBy((item) -> {
                        OrderStatisticsDept statisticsDept = new OrderStatisticsDept();
                        statisticsDept.setAppName(item.getAppName());
                        statisticsDept.setMchName(item.getMchName());
                        statisticsDept.setParentName(item.getParentName());
                        return statisticsDept;
                    }, Collectors.summingDouble(OrderStatisticsDept::getAmount)));


                    Map<String, Double> accountDetail = new HashMap<>();
                    map1.entrySet().forEach(entry1 -> {
                        accountDetail.put(entry1.getKey().getAppName(), entry1.getValue());
                    });
                    AccountRQ.DepartMentAccountRQ departMentAccountRQ = new AccountRQ.DepartMentAccountRQ();
                    departMentAccountRQ.setLevelName("集团");
                    departMentAccountRQ.setTotalAccountForDept(accountRQ.getTotalAccountForCompany());
                    departMentAccountRQ.setDeptName("集团");
                    departMentAccountRQ.setAccountDetail(accountDetail);

                    accountRQ.getDepartMentAccountRQList().add(departMentAccountRQ);

                    //部门账
                    Map<String, Double> deptMap = entry.getValue().stream()
                            .collect(Collectors.groupingBy(OrderStatisticsDept::getDeptName
                                    , Collectors.summingDouble(OrderStatisticsDept::getAmount)));

                    deptMap.entrySet().forEach(item -> {
                        AccountRQ.DepartMentAccountRQ departMentAccountRQ2 = new AccountRQ.DepartMentAccountRQ();
                        departMentAccountRQ2.setLevelName("部门");
                        departMentAccountRQ2.setTotalAccountForDept(item.getValue());
                        departMentAccountRQ2.setDeptName(item.getKey());

                        Map<String, Double> map2 = entry.getValue().stream().filter(el -> el.getDeptName().equals(item.getKey()))
                                .collect(Collectors.groupingBy(OrderStatisticsDept::getAppName
                                        , Collectors.summingDouble(OrderStatisticsDept::getAmount)));

                        Map<String, Double> accountDetai2 = new HashMap<>();
                        map2.entrySet().forEach(entry1 -> {
                            accountDetai2.put(entry1.getKey(), entry1.getValue());
                        });

                        departMentAccountRQ2.setAccountDetail(accountDetai2);
                        accountRQ.getDepartMentAccountRQList().add(departMentAccountRQ2);

                    });

                    accountRQList.add(accountRQ);
                }
        );

        return accountRQList;
    }

}