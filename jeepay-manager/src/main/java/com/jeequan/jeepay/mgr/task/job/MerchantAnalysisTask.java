package com.jeequan.jeepay.mgr.task.job;

import cn.hutool.core.collection.CollectionUtil;
import com.jeequan.jeepay.core.aop.MethodLog;
import com.jeequan.jeepay.core.entity.OrderStatisticsCompany;
import com.jeequan.jeepay.core.entity.OrderStatisticsMerchant;
import com.jeequan.jeepay.mgr.task.AbstractAnalysisTask;
import com.jeequan.jeepay.service.impl.OrderStatisticsMerchantService;
import com.jeequan.jeepay.service.impl.PayOrderExtendService;
import com.jeequan.jeepay.service.impl.PayOrderService;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component("merchatAnalysisTask")
@Configuration
public class MerchantAnalysisTask extends AbstractAnalysisTask {

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private PayOrderExtendService payOrderExtendService;

    @Autowired
    private OrderStatisticsMerchantService orderStatisticsMerchantService;

    /**
     * 根据周期段进行分析
     *
     * @param period 1表示天，2表示周 ，3表示月 4表示年
     */
    @Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    @MethodLog(remark = "商户账单报表分析作业")
    protected void process(int period) throws Exception {

        MutablePair<String, String> timePair = this.getPeriod(period);//时间段
        Long analyseId = System.currentTimeMillis();//产生版本号
        ;
        List<OrderStatisticsMerchant> orderStatisticsDeptList = orderStatisticsMerchantService.selectOrderCountByMerchant(timePair.left, timePair.right);

        if (!CollectionUtil.isEmpty(orderStatisticsDeptList)) {

            orderStatisticsDeptList.forEach(item -> {
                item.setStaticState(OrderStatisticsCompany.ACCOUNT_STATE_NUN);
                item.setAmountInfact(0L);
                item.setRemark("商户账单报表分析作业");
                item.setAnalyseId(analyseId);
            });
            orderStatisticsMerchantService.saveBatch(orderStatisticsDeptList, 200);
        }
    }

}