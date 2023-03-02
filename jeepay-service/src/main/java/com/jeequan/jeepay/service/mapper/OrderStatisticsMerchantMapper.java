package com.jeequan.jeepay.service.mapper;

import com.jeequan.jeepay.core.entity.OrderStatisticsDept;
import com.jeequan.jeepay.core.entity.OrderStatisticsMerchant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单分析主表 Mapper 接口
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2023-02-17
 */
public interface OrderStatisticsMerchantMapper extends BaseMapper<OrderStatisticsMerchant> {

    /**
     * 查询各档口的收入
     * @param param
     * @return
     */
    List<OrderStatisticsMerchant> selectOrderCountByMerchant(Map param);

    /**
     * 批量插入数据
     * @param orderStatisticsMerchantList
     * @return
     */
    public int insertBatch(List<OrderStatisticsMerchant> orderStatisticsMerchantList);
}
