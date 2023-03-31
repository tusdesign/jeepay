package com.jeequan.jeepay.mgr.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jeequan.jeepay.core.entity.OrderStatisticsDept;
import com.jeequan.jeepay.mgr.rqrs.AccountForDepartRq;
import com.jeequan.jeepay.mgr.rqrs.AccountForDepartmentRq;
import com.jeequan.jeepay.mgr.rqrs.AccountForTenantRq;
import com.jeequan.jeepay.service.impl.OrderStatisticsDeptService;
import lombok.extern.slf4j.Slf4j;
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
     * @return List<AccountRQ>
     */
    public List<AccountForDepartRq> getAccountList(int month) {

        List<AccountForDepartRq> accountRQList = new ArrayList<>();

        LocalDate ldt = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateStart = LocalDateTime.of(ldt.getYear(), month, 1, 0, 0, 0);
        LocalDateTime dateEnd = LocalDateTime.of(ldt.getYear(), month, getDaysByYearMonth(ldt.getYear(), month), 23, 59, 59);

        QueryWrapper<OrderStatisticsDept> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("created_at", dtf.format(dateStart), dateEnd);
        queryWrapper.eq(month > 0, "MONTH(FROM_UNIXTIME(analyse_id/1000))", month);
        List<OrderStatisticsDept> orderStatisticsDeptList = statisticsDeptService.list(queryWrapper);

        Map<OrderStatisticsDept, List<OrderStatisticsDept>> orderDepatListMap =
                orderStatisticsDeptList.stream()
                        .sorted(Comparator.comparing(OrderStatisticsDept::getAnalyseId).reversed())
                        .collect(Collectors.groupingBy((item) -> {
                            OrderStatisticsDept dept = new OrderStatisticsDept();
                            dept.setDeptName(item.getDeptName());
                            dept.setDeptId(item.getDeptId());
                            dept.setParentId(item.getParentId());
                            dept.setAppId(item.getAppId());
                            dept.setAppName(item.getAppName());
                            dept.setMchName(item.getMchName());
                            dept.setMchNo(item.getMchNo());
                            return dept;
                        }));

        List<OrderStatisticsDept> orderDeptList = new ArrayList<>();
        orderDepatListMap.entrySet().forEach(item -> {
            OrderStatisticsDept dept = new OrderStatisticsDept();
            dept.setDeptName(item.getKey().getDeptName());
            dept.setDeptId(item.getKey().getDeptId());
            dept.setParentId(item.getKey().getParentId());
            dept.setAppId(item.getKey().getAppId());
            dept.setAppName(item.getKey().getAppName());
            dept.setMchName(item.getKey().getMchName());
            dept.setMchNo(item.getKey().getMchNo());
            dept.setAnalyseId(item.getValue().stream().findFirst().get().getAnalyseId());
            dept.setAmount(item.getValue().stream().findFirst().get().getAmount());
            dept.setParentName(item.getValue().stream().findFirst().get().getParentName());
            dept.setExtType(item.getValue().stream().findFirst().get().getExtType());
            orderDeptList.add(dept);
        });

        Map<String, List<OrderStatisticsDept>> companyMap = orderDeptList.stream().distinct()
                .collect(Collectors.groupingBy(OrderStatisticsDept::getParentName));


        companyMap.entrySet().forEach(entry -> {

                    AccountForDepartRq accountRQ = new AccountForDepartRq();
                    accountRQ.setCompanyName(entry.getKey());
                    accountRQ.setTotalAccountForCompany(entry.getValue().stream().mapToDouble(OrderStatisticsDept::getAmount).sum());
                    accountRQ.setDepartMentAccountRQList(new ArrayList<AccountForDepartRq.DepartMentAccountRQ>());

                    Map<OrderStatisticsDept, Double> map1 = entry.getValue().stream().collect(Collectors.groupingBy((item) -> {
                        OrderStatisticsDept statisticsDept = new OrderStatisticsDept();
                        statisticsDept.setAppName(item.getAppName());
                        statisticsDept.setMchName(item.getMchName());
                        statisticsDept.setParentName(item.getParentName());//公司或集团名称
                        return statisticsDept;
                    }, Collectors.summingDouble(OrderStatisticsDept::getAmount)));

                    Map<String, Double> accountDetail = new HashMap<>();
                    map1.entrySet().forEach(entry1 -> {
                        accountDetail.put(entry1.getKey().getAppName(), entry1.getValue());
                    });
                    AccountForDepartRq.DepartMentAccountRQ departMentAccountRQ = new AccountForDepartRq.DepartMentAccountRQ();
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
                        AccountForDepartRq.DepartMentAccountRQ departMentAccountRQ2 = new AccountForDepartRq.DepartMentAccountRQ();
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

        QueryWrapper<OrderStatisticsDept> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("created_at", dtf.format(dateStart), dateEnd);
        queryWrapper.eq(month > 0, "MONTH(FROM_UNIXTIME(analyse_id/1000))", month);
        List<OrderStatisticsDept> orderStatisticsDeptList = statisticsDeptService.list(queryWrapper);

        Map<Long, List<OrderStatisticsDept>> orderDepatListMap =
                orderStatisticsDeptList.stream().collect(Collectors.groupingBy(OrderStatisticsDept::getAnalyseId));

        Set<Long> set = orderDepatListMap.keySet();
        Object[] obj = set.toArray();
        Arrays.sort(obj);

        //最新版本的统计数据
        List<OrderStatisticsDept> orderStatisticsDeptsNews = orderDepatListMap.get(obj[obj.length - 1]);

        //按公司，消费类别两层分组
        Map<String, Map<String, List<OrderStatisticsDept>>> companyMap = orderStatisticsDeptsNews.stream()
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
                    entry.getValue().entrySet().forEach(sub -> {

                        AccountForDepartmentRq accountForDepartmentRq = new AccountForDepartmentRq();
                        accountForDepartmentRq.setAppName(sub.getKey());
                        Map<String, Double> accountDeptMap = sub.getValue().stream()
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
