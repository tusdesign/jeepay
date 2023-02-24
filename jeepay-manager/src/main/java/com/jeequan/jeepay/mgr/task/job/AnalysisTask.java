package com.jeequan.jeepay.mgr.task.job;

import com.jeequan.jeepay.service.impl.OrderStatisticsCompanyService;
import com.jeequan.jeepay.service.impl.OrderStatisticsDeptService;
import com.jeequan.jeepay.service.impl.OrderStatisticsMerchantService;
import com.jeequan.jeepay.service.impl.PayOrderExtendService;
import org.springframework.beans.factory.annotation.Autowired;

public class AnalysisTask {

    @Autowired
    private PayOrderExtendService payOrderExtendService;

    @Autowired
    private OrderStatisticsCompanyService orderStatisticsCompanyService;

    @Autowired
    private OrderStatisticsDeptService orderStatisticsDeptService;

    @Autowired
    private OrderStatisticsMerchantService orderStatisticsMerchantService;

    public void Analyse(){
        //企业结算
    }
}
