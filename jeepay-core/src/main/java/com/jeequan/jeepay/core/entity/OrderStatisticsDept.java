package com.jeequan.jeepay.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 订单分析部门表
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2023-02-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_order_statistics_dept")
public class OrderStatisticsDept implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主表分析标识符
     */
    private Long analyseId;

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
     * 公司名称
     */
    @TableField(exist=false)
    private String companyName;

    /**
     * 部门id
     */
    @TableField(exist=false)
    private String deptId;


    /**
     * 部门账单金额,单位分
     */
    private double amount;

    /**
     * 创建时间
     */
    private Date createdAt;

}