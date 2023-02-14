package com.jeequan.jeepay.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单分析部门表
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2023-02-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_order_statistics_dept")
public class OrderStatisticsDept implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 订单分析报表主键ID
     */
    private String statisticsDeptId;

    /**
     * 分析主表Id
     */
    private String primaryStatisticsId;

    /**
     * 商户号
     */
    private String mchNo;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 商户名称
     */
    private String mchName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 类型: 1-商户, 2-入住企业
     */
    private Byte statisticType;

    /**
     * 支付金额,单位分
     */
    private Long amount;

    /**
     * 创建时间
     */
    private Date createdAt;


}
