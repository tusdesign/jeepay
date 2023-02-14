package com.jeequan.jeepay.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单分析主表
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2023-02-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_order_statistics_company")
public class OrderStatisticsCompany implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 订单分析报表主键ID
     */
    private String statisticsCompanyId;

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
     * 组织名称，部门或是入驻企业名称
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
     * 实付金额
     */
    private Long amountInfact;

    /**
     * 结账状态, 0-已结账,  1-未结账
     */
    private Byte staticState;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createdAt;


}
