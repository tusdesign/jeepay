package com.jeequan.jeepay.mgr.task.job;

import cn.hutool.core.collection.CollectionUtil;
import com.jeequan.jeepay.core.aop.MethodLog;
import com.jeequan.jeepay.core.cache.RedisUtil;
import com.jeequan.jeepay.core.entity.OrderStatisticsCompany;
import com.jeequan.jeepay.core.entity.OrderStatisticsDept;
import com.jeequan.jeepay.mgr.task.AbstractAnalysisTask;
import com.jeequan.jeepay.service.impl.OrderStatisticsCompanyService;
import com.jeequan.jeepay.service.impl.OrderStatisticsDeptService;
import com.jeequan.jeepay.service.impl.PayOrderExtendService;
import com.jeequan.jeepay.service.impl.PayOrderService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component("companyAnalysisTask")
@Configuration
public class CompanyAnalysisTask extends AbstractAnalysisTask {

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


    /**
     * 根据周期段进行分析
     *
     * @param period 1表示天，2表示周 ，3表示月 4表示年
     */
    //@Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    protected void process(String period) throws Exception {

        MutablePair<String, String> timePair = this.getPeriod(period);//时间段
        Long analyseId = System.currentTimeMillis(); //产生版本号

        List<OrderStatisticsDept> orderStatisticsDeptList = payOrderService.selectOrderCountByDept(timePair.left, timePair.right);

        if (!CollectionUtil.isEmpty(orderStatisticsDeptList)) {
            orderStatisticsDeptList.forEach(item -> {
                //去启迪查询部门信息
                MutablePair<String, String> mutablePair = getDept(item.getDeptId());
                item.setDeptName(mutablePair.right);
                item.setCompanyName(mutablePair.left);
                item.setAnalyseId(analyseId);
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
                    company.setRemark("企业账单分析");
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

        String[] nameArray;

        //先去缓存中查一下
        String organization = RedisUtil.getString(deptId);
        if (organization == null || organization.isEmpty()) {
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

                    RedisUtil.setString(deptId, fullPath, 30, TimeUnit.DAYS);

                    nameArray = StringUtils.split(fullPath, "/");
                    return MutablePair.of(nameArray[0], nameArray[1]);
                }
            }
        } else {
            nameArray = StringUtils.split(organization, "/");
            return MutablePair.of(nameArray[0], nameArray[1]);

        }
        return MutablePair.of("未知", "未知");
    }
}
