package com.jeequan.jeepay.mgr.task.job;

import ch.qos.logback.core.joran.conditional.ElseAction;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.druid.sql.ast.statement.SQLIfStatement;
import com.jeequan.jeepay.core.entity.OrderStatisticsDept;
import com.jeequan.jeepay.mgr.util.TimeUtil;
import com.jeequan.jeepay.service.impl.*;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("analysisTask")
@Configuration
public class AnalysisTask {

    @Value(value = "${qiDi.gateWay.url}")
    private String gateWay;

    @Value(value = "${qiDi.gateWay.secret-key}")
    private String secretKey;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private PayOrderExtendService payOrderExtendService;

    @Autowired
    private OrderStatisticsCompanyService orderStatisticsCompanyService;

    @Autowired
    private OrderStatisticsDeptService orderStatisticsDeptService;

    @Autowired
    private OrderStatisticsMerchantService orderStatisticsMerchantService;

    /**
     * 根据周期段进行分析
     *
     * @param cycle 1表示年，2表示月 3表示周
     */
    @Transactional(rollbackFor = Exception.class)
    public void Analyse(int cycle) throws Exception {

        String createTimeStart = "";//开始时间
        String createTimeEnd = "";//结束时间

        if (1 == cycle) {
            createTimeStart = TimeUtil.getBeforeFirstYearDate();
            createTimeEnd = TimeUtil.getBeforeLastYearDate();
        } else if (2 == cycle) {
            createTimeStart = TimeUtil.getBeforeFirstMonthDate();
            createTimeEnd = TimeUtil.getBeforeLastMonthDate();
        } else if (3 == cycle) {
            createTimeStart = TimeUtil.getBeforeFirstDayDate();
            createTimeEnd = TimeUtil.getBeforeLastDayDate();
        }

        //产生版本号
        Long analyseId=0L;
        List<OrderStatisticsDept> orderStatisticsDeptList = payOrderService.selectOrderCountByDept(createTimeStart, createTimeEnd);

        if (!CollectionUtil.isEmpty(orderStatisticsDeptList)) {
            orderStatisticsDeptList.forEach(item -> {
                //去启迪查询部门信息
                MutablePair<String, Object> mutablePair = getDept(item.getDeptId());
                item.setDeptName("");
                item.setCompanyName("");
            });
            boolean stepOne = orderStatisticsDeptService.saveBatch(orderStatisticsDeptList, 200);

            if (stepOne) {

            }
        }
    }

    /**
     * 根据部门Id得到部门信息
     * @param deptId
     * @return MutablePair<String, Object>
     */
    private MutablePair<String, Object> getDept(String deptId) {
        return MutablePair.of("deptName", "compnayName");
    }

}
