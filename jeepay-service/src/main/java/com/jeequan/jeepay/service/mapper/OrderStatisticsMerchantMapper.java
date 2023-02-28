package com.jeequan.jeepay.service.mapper;

import com.jeequan.jeepay.core.entity.OrderStatisticsMerchant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 订单分析主表 Mapper 接口
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2023-02-17
 */
public interface OrderStatisticsMerchantMapper extends BaseMapper<OrderStatisticsMerchant> {

    public int insertBatch(List<OrderStatisticsMerchant> orderStatisticsMerchantList);
}
