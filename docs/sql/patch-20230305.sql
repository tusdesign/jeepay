
--
-- 订单拓展表`t_pay_order_extend`
--

DROP TABLE IF EXISTS `t_pay_order_extend`;
CREATE TABLE `t_pay_order_extend` (
  `pay_order_id` varchar(30)  NOT NULL COMMENT '支付订单号,关联主表',
  `pid` varchar(36) DEFAULT '' COMMENT '人员Id',
  `businessId` varchar(36) DEFAULT '' COMMENT '业务id',
  `dealType` varchar(30)  DEFAULT '' COMMENT '交易类型：PERSONAL-个人支付,DEPARTMENTAL-部门支付',
  `deptId` varchar(36) DEFAULT '' COMMENT '部门Id',
  `account_state` tinyint DEFAULT '0' COMMENT '结账状态：0-未结账 1-已结账',
  `created_at` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  PRIMARY KEY (`pay_order_id`),
  KEY `created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单拓展表';


--
-- 企业账单分析表 `t_order_statistics_company`
--

DROP TABLE IF EXISTS `t_order_statistics_company`;
CREATE TABLE `t_order_statistics_company` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `mch_no` varchar(64) NOT NULL COMMENT '商户号',
  `app_id` varchar(64) NOT NULL COMMENT '应用ID',
  `app_name` varchar(64) NOT NULL COMMENT '应用名称',
  `mch_name` varchar(30) NOT NULL COMMENT '商户名称',
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '企业账单金额,单位分',
  `dept_name` varchar(100) NOT NULL COMMENT '组织名称：企业名称或者部门名称',
  `amount_infact` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实付金额',
  `analyse_id` bigint DEFAULT NULL COMMENT '报表分析标识',
  `static_state` tinyint NOT NULL DEFAULT '0' COMMENT '结账状态, 0-已结账,  1-未结账',
  `remark` varchar(512) NOT NULL COMMENT '备注',
  `created_at` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业账单分析表';


--
-- 部门账单分析表 `t_order_statistics_dept`
--

DROP TABLE IF EXISTS `t_order_statistics_dept`;
CREATE TABLE `t_order_statistics_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `analyse_id` bigint NOT NULL COMMENT '企业分析表标识符',
  `mch_no` varchar(64) NOT NULL COMMENT '商户号',
  `app_id` varchar(64) NOT NULL COMMENT '应用ID',
  `app_name` varchar(64) NOT NULL COMMENT '应用名称',
  `mch_name` varchar(30) NOT NULL COMMENT '商户名称',
  `dept_name` varchar(100) NOT NULL COMMENT '部门名称',
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '部门账单金额,单位分',
  `created_at` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门账单分析表';


--
-- 商户订单分析表 `t_order_statistics_merchant`
--

DROP TABLE IF EXISTS `t_order_statistics_merchant`;
CREATE TABLE `t_order_statistics_merchant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `mch_no` varchar(64) NOT NULL COMMENT '商户号',
  `app_id` varchar(64) NOT NULL COMMENT '应用ID',
  `app_name` varchar(64) NOT NULL COMMENT '应用名称',
  `mch_name` varchar(30) NOT NULL COMMENT '商户名称',
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商户订单总额,单位分',
  `amount_infact` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实际结算金额',
  `analyse_id` bigint DEFAULT NULL COMMENT '报表分析标识',
  `static_state` tinyint NOT NULL DEFAULT '0' COMMENT '结账状态, 0-已结账,  1-未结账',
  `remark` varchar(512) NOT NULL COMMENT '备注',
  `created_at` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商户订单分析表';


--
-- job表 `t_sys_job`
--
DROP TABLE IF EXISTS `t_sys_job`;
CREATE TABLE `t_sys_job` (
  `job_id` varchar(36) NOT NULL COMMENT '任务Id',
  `bean_name` varchar(100) NOT NULL COMMENT 'bean名称',
  `method_name` varchar(100) NOT NULL COMMENT '方法名称',
  `method_params` varchar(255) DEFAULT NULL COMMENT '方法参数',
  `cron_expression` varchar(255) NOT NULL COMMENT 'cron表达式',
  `remark` varchar(500) DEFAULT '' COMMENT '备注说明',
  `job_status` tinyint NOT NULL COMMENT '状态(1:正常, 0:暂停)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- job日志表 `t_sys_job_log`
--
DROP TABLE IF EXISTS `t_sys_job_log`;
CREATE TABLE `t_sys_job_log` (
  `id` varchar(36) NOT NULL COMMENT '主键编号',
  `job_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务id',
  `exectue_end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `exectue_start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `exectue_result` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'Y成功，N失败',
  `remark` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务日志表 ';