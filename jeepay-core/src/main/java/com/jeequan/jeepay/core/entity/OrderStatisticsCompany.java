package com.jeequan.jeepay.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
@TableName("t_order_statistics_company")
public class OrderStatisticsCompany implements Serializable {

    private static final long serialVersionUID=1L;

    public static final byte ACCOUNT_STATE_NUN = 0;//已结账
    public static final byte ACCOUNT_STATE_FINISHED = 1;  //未结账

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
     * 企业账单金额,单位分
     */
    private Long amount;

    /**
     * 组织名称：企业名称或者部门名称
     */
    private String deptName;

    /**
     * 实付金额
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