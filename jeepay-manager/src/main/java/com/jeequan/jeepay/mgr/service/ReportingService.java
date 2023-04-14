package com.jeequan.jeepay.mgr.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jeequan.jeepay.core.entity.OrderStatisticsDept;
import com.jeequan.jeepay.core.exception.BizException;
import com.jeequan.jeepay.mgr.rqrs.AccountForDepartRq;
import com.jeequan.jeepay.mgr.rqrs.AccountForDepartmentRq;
import com.jeequan.jeepay.mgr.rqrs.AccountForTenantRq;
import com.jeequan.jeepay.service.impl.OrderStatisticsDeptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
     * @return List<AccountForTenantRq>
     */
    public List<AccountForTenantRq> getAccountForTenants(int month) {

        List<AccountForTenantRq> accountRQList = new ArrayList<>();

        LocalDate ldt = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateStart = LocalDateTime.of(ldt.getYear(), month, 1, 0, 0, 0);
        LocalDateTime dateEnd = LocalDateTime.of(ldt.getYear(), month, getDaysByYearMonth(ldt.getYear(), month), 23, 59, 59);

        //最新版本的统计数据
        QueryWrapper<OrderStatisticsDept> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("created_at", dtf.format(dateStart), dateEnd);
        queryWrapper.eq(month > 0, "MONTH(FROM_UNIXTIME(analyse_id/1000))", month);
        queryWrapper.orderByDesc("analyse_id");
        queryWrapper.last("limit 1");
        OrderStatisticsDept statisticsDept = statisticsDeptService.getOne(queryWrapper);
        if (Objects.isNull(statisticsDept)) {
            throw new BizException("查找的月份账单还没有生成!");
        }

        List<OrderStatisticsDept> orderStatisticsDeptList = statisticsDeptService.list(
                new QueryWrapper<OrderStatisticsDept>()
                        .eq("analyse_id", statisticsDept.getAnalyseId())
                        .and(e -> e.ne("dept_name", "")
                                .isNotNull("dept_name"))
        );

        //按公司，消费类别两层分组
        Map<String, Map<String, List<OrderStatisticsDept>>> companyMap = orderStatisticsDeptList.stream()
                .collect(Collectors.groupingBy(OrderStatisticsDept::getParentName,
                        Collectors.groupingBy(OrderStatisticsDept::getAppName)));

        //遍历公司列表
        companyMap.entrySet().forEach(entry -> {
                    AccountForTenantRq accountRQ = new AccountForTenantRq();
                    accountRQ.setAccountTime(new Date());
                    accountRQ.setGroupName(entry.getKey());
                    accountRQ.setAccountForDepartmentRqs(new ArrayList<AccountForDepartmentRq>());

                    //公司下的所有类别统计
                    Map<String, Double> cmpAccountDetailMap = new HashMap<>();
                    entry.getValue().entrySet().forEach(sub -> {
                        cmpAccountDetailMap.put(sub.getKey(), sub.getValue().stream().mapToDouble(OrderStatisticsDept::getAmount).sum());
                    });
                    accountRQ.setCmpAccountDetailMap(cmpAccountDetailMap);

                    //部门消费分类列表统计
                    List<AccountForDepartmentRq> accountForDepartmentRqs = new ArrayList<>();

                    //拓展type没有分类的情况
                    Map<String, List<OrderStatisticsDept>> orderStaticDepatMapNoType = entry.getValue().entrySet().stream()
                            .filter(item -> item.getValue()
                                    .stream()
                                    .filter(sub -> StringUtils.isEmpty(sub.getExtType()))
                                    .findAny().isPresent())
                            .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

                    orderStaticDepatMapNoType.entrySet().forEach(sub -> {

                        AccountForDepartmentRq accountForDepartmentRq = new AccountForDepartmentRq();
                        accountForDepartmentRq.setAppName(sub.getKey());

                        Map<String, Double> accountDeptMap = sub.getValue().stream().filter(item -> StringUtils.isEmpty(item.getExtType()))
                                .collect(Collectors.groupingBy(OrderStatisticsDept::getDeptName, Collectors.summingDouble(OrderStatisticsDept::getAmount)));
                        Map<String, Double> orgAccountDetailMap = new HashMap<>();
                        accountDeptMap.entrySet().forEach(item -> {
                            orgAccountDetailMap.put(item.getKey(), item.getValue());
                        });
                        accountForDepartmentRq.setOrgAccountDetailMap(orgAccountDetailMap);
                        accountForDepartmentRq.setAccountTime(new Date());
                        accountForDepartmentRq.setTotalAccountForApp(sub.getValue().stream().mapToDouble(OrderStatisticsDept::getAmount).sum());
                        accountForDepartmentRqs.add(accountForDepartmentRq);
                    });


                    //拓展type有分类的情况
                    Map<String, List<OrderStatisticsDept>> orderStaticDepatMapType = entry.getValue().entrySet().stream()
                            .filter(item -> item.getValue()
                                    .stream()
                                    .filter(sub -> !StringUtils.isEmpty(sub.getExtType()))
                                    .findAny().isPresent())

                            .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

                    orderStaticDepatMapType.entrySet().forEach(sub -> {

                        AccountForDepartmentRq accountForDepartmentRq = new AccountForDepartmentRq();
                        accountForDepartmentRq.setAppName(sub.getKey());

                        Map<String, Map<String, Double>> accountTypeDeptMap = sub.getValue()
                                .stream()
                                .filter(item -> !StringUtils.isEmpty(item.getExtType()))
                                .collect(Collectors.groupingBy(OrderStatisticsDept::getDeptName
                                        , Collectors.groupingBy(OrderStatisticsDept::getExtType, Collectors.summingDouble(OrderStatisticsDept::getAmount))));

                        Map<String, Map<String, Double>> orgAccountTypeDetailMap = new HashMap<>();
                        accountTypeDeptMap.entrySet().forEach(item -> {
                            orgAccountTypeDetailMap.put(item.getKey(), item.getValue());
                        });
                        accountForDepartmentRq.setTypeDetailMap((orgAccountTypeDetailMap));
                        accountForDepartmentRq.setAccountTime(new Date());
                        accountForDepartmentRq.setTotalAccountForApp(sub.getValue().stream().mapToDouble(OrderStatisticsDept::getAmount).sum());
                        accountForDepartmentRqs.add(accountForDepartmentRq);
                    });

                    accountRQ.getAccountForDepartmentRqs().addAll(accountForDepartmentRqs);
                    accountRQ.setTotalAccountForTenant(accountForDepartmentRqs.stream().mapToDouble(AccountForDepartmentRq::getTotalAccountForApp).sum());
                    accountRQList.add(accountRQ);
                }
        );
        return accountRQList;
    }

    private int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }
}
