package com.jeequan.jeepay.mgr.task.job;

import ch.qos.logback.core.joran.conditional.ElseAction;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.druid.sql.ast.statement.SQLIfStatement;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jeequan.jeepay.core.entity.MchApp;
import com.jeequan.jeepay.core.entity.OrderStatisticsCompany;
import com.jeequan.jeepay.core.entity.OrderStatisticsDept;
import com.jeequan.jeepay.mgr.util.TimeUtil;
import com.jeequan.jeepay.service.impl.*;
import com.jeequan.jeepay.util.JeepayKit;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component("analysisTask")
@Configuration
public class AnalysisTask {

    @Value(value = "${qiDi.gateWay.url}")
    private String gateWay;

    @Value(value = "${qiDi.gateWay.secret-key}")
    private String secretKey;

    @Resource
    private RestTemplate restTemplate;

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
        Long analyseId = 0L;
        List<OrderStatisticsDept> orderStatisticsDeptList = payOrderService.selectOrderCountByDept(createTimeStart, createTimeEnd);

        if (!CollectionUtil.isEmpty(orderStatisticsDeptList)) {
            orderStatisticsDeptList.forEach(item -> {
                //去启迪查询部门信息
                MutablePair<String, String> mutablePair = getDept(item.getDeptId());
                item.setDeptName(mutablePair.right);
                item.setCompanyName(mutablePair.left);
            });
            boolean stepOne = orderStatisticsDeptService.saveBatch(orderStatisticsDeptList, 200);
            if (stepOne) {
                Map<OrderStatisticsCompany, Long> map = orderStatisticsDeptList.stream().collect(Collectors.groupingBy((item) -> {
                    OrderStatisticsCompany company = new OrderStatisticsCompany();
                    company.setAppId(item.getAppId());
                    company.setAppName(item.getAppName());
                    company.setMchNo(item.getMchNo());
                    company.setMchName(item.getMchName());
                    company.setStaticState(OrderStatisticsCompany.ACCOUNT_STATE_NUN);
                    company.setAmountInfact(0L);
                    company.setAnalyseId(analyseId);
                    company.setDeptName(item.getCompanyName());
                    return company;
                }, Collectors.summingLong(OrderStatisticsDept::getAmount)));

                List<OrderStatisticsCompany> orderStatisticsCompanyList = new ArrayList<>();
                map.forEach((k, v) -> {
                    k.setAmount(v);
                    orderStatisticsCompanyList.add(k);
                });
                orderStatisticsCompanyService.saveBatch(orderStatisticsCompanyList);
            }
        }
    }


    /**
     * 根据部门Id得到部门信息
     *
     * @param deptId
     * @return MutablePair<String, Object>
     */
    @SneakyThrows
    private MutablePair<String, String> getDept(String deptId) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-KEY", secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request =
                new HttpEntity<String>(null, headers);
        ResponseEntity<Map> responseMap = restTemplate.exchange(gateWay + MessageFormat.format("/groups/{0}", deptId), HttpMethod.GET, request, Map.class);

        if (responseMap.getStatusCode().equals(HttpStatus.OK)) {
            Map<String, Object> responseBody = responseMap.getBody();
            if (!responseBody.isEmpty() && responseBody.containsKey("path")) {
                String fullPath = String.valueOf(responseBody.get("path"));
                String[] nameArray = StringUtils.split(fullPath, "/");
                return MutablePair.of(nameArray[0], nameArray[1]);
            }
        }
        return MutablePair.of("未知", "未知");
    }

}
