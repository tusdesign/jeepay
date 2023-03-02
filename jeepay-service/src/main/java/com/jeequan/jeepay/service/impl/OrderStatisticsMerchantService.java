package com.jeequan.jeepay.service.impl;

import com.jeequan.jeepay.core.entity.OrderStatisticsDept;
import com.jeequan.jeepay.core.entity.OrderStatisticsMerchant;
import com.jeequan.jeepay.service.mapper.OrderStatisticsMerchantMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单分析主表 服务实现类
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2023-02-17
 */
@Service
public class OrderStatisticsMerchantService extends ServiceImpl<OrderStatisticsMerchantMapper, OrderStatisticsMerchant>{

    public List<OrderStatisticsMerchant> selectOrderCountByMerchant(String createTimeStart, String createTimeEnd)
    {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("createTimeStart",createTimeStart);
        paramMap.put("createTimeEnd",createTimeEnd);
        return this.getBaseMapper().selectOrderCountByMerchant(paramMap);
    }
}
