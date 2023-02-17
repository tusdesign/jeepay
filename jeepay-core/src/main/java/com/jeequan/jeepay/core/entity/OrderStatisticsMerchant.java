package com.jeequan.jeepay.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2023-02-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_order_statistics_merchant")
public class OrderStatisticsMerchant implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 商户账单金额,单位分
     */
    private Long amount;

    /**
     * 实际结算金额
     */
    private Long amountInfact;

    /**
     * 报表分析标识
     */
    private Long analyseId;

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
