package com.jeequan.jeepay.service.mapper;

import com.jeequan.jeepay.core.entity.PayOrderExtend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 支付订单表 Mapper 接口
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2023-02-03
 */
public interface PayOrderExtendMapper extends BaseMapper<PayOrderExtend> {

    int updateRefundAmountAndCount(@Param("payOrderId") String payOrderId, @Param("currentRefundAmount") Long currentRefundAmount);
}
