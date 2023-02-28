package com.jeequan.jeepay.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeequan.jeepay.core.entity.OrderStatisticsCompany;
import com.jeequan.jeepay.core.entity.OrderStatisticsDept;

import java.util.List;

/**
 * <p>
 * 订单分析部门表 Mapper 接口
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2023-02-17
 */
public interface OrderStatisticsDeptMapper extends BaseMapper<OrderStatisticsDept> {

    public int insertBatch(List<OrderStatisticsDept> orderStatisticsDeptList);

}
