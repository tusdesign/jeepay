package com.jeequan.jeepay.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 支付订单拓展表
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2023-02-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_pay_order_extend")
public class PayOrderExtend implements Serializable {

    private static final long serialVersionUID=1L;

    public static final byte ACCOUNT_STATE_NUN = 0; //未结账
    public static final byte ACCOUNT_STATE_FINISHED = 1; //已结账

    /**
     * 支付订单号,关联主表
     */
    private String payOrderId;


    /**
     * 标识位：费用类型
     */
    @TableField("ext_type")
    private String extType;

    /**
     * 人员Id
     */
    private String pid;

    /**
     * 业务id
     */
    @TableField("businessId")
    private String businessId;

    /**
     * 交易类型：PERSONAL-个人支付,DEPARTMENTAL-部门支付
     */
    @TableField("dealType")
    private String dealType;

    /**
     * 部门Id
     */
    @TableField("deptId")
    private String deptId;

    /**
     * 结账状态：0-未结账 1-已结账
     */
    private Byte accountState;

    /**
     * 创建时间
     */
    private Date createdAt;

}