package com.jeequan.jeepay.mgr.task.job;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.jeequan.jeepay.core.aop.Action;
import com.jeequan.jeepay.core.entity.OrderStatisticsCompany;
import com.jeequan.jeepay.core.entity.OrderStatisticsMerchant;
import com.jeequan.jeepay.core.entity.SysJob;
import com.jeequan.jeepay.service.impl.OrderStatisticsMerchantService;
import com.jeequan.jeepay.service.impl.PayOrderExtendService;
import com.jeequan.jeepay.service.impl.PayOrderService;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;


@Component("merchatAnalysisJob")
@Configuration
public class MerchantAnalysisJob extends AbstractAnalysisJob {

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private PayOrderExtendService payOrderExtendService;

    @Autowired
    private OrderStatisticsMerchantService orderStatisticsMerchantService;

    /**
     * 根据周期段进行分析
     *
     * @param job 1表示天，2表示周 ，3表示月 4表示年 ,5表示自定义时间段
     */
    @Override
    @Action("商户账单报表分析")
    @Transactional(rollbackFor = Exception.class)
    public void process(SysJob job) throws Exception {

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        JSONObject jsonObject= JSONObject.parseObject(job.getMethodParams());
        //时间段
        MutablePair<String, String> timePair = this.getPeriod(
                jsonObject.getString("period")
                , dateFormat.format(jsonObject.getDate("timeStart"))
                , dateFormat.format(jsonObject.getDate("timeEnd")));

        //产生版本号
        Long analyseId = System.currentTimeMillis();
        ;
        List<OrderStatisticsMerchant> orderStatisticsDeptList = orderStatisticsMerchantService.selectOrderCountByMerchant(timePair.left, timePair.right);

        if (!CollectionUtil.isEmpty(orderStatisticsDeptList)) {

            orderStatisticsDeptList.forEach(item -> {
                item.setStaticState(OrderStatisticsCompany.ACCOUNT_STATE_NUN);
                item.setAmountInfact(0D);
                item.setRemark("商户账单报表分析作业");
                item.setAnalyseId(analyseId);
            });
            orderStatisticsMerchantService.saveBatch(orderStatisticsDeptList, 200);
        }
    }

}