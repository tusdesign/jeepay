ALTER TABLE `jeepaydb`.`t_pay_order_extend`
ADD COLUMN `ext_type` VARCHAR(30) NULL DEFAULT '' COMMENT '标识位:费用类型, 部门标注' AFTER `created_at`;

ALTER TABLE `jeepaydb`.`t_pay_order_extend`
CHANGE COLUMN `deptId` `deptId` VARCHAR(50) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NULL DEFAULT '' COMMENT '部门Id' ;


ALTER TABLE `jeepaydb`.`t_transfer_order`
CHANGE COLUMN `ext_param` `ext_param` VARCHAR(256) NULL DEFAULT NULL COMMENT '商户扩展参数' ;


ALTER TABLE `jeepaydb`.`t_refund_order`
CHANGE COLUMN `ext_param` `ext_param` VARCHAR(256) NULL DEFAULT NULL COMMENT '扩展参数' ;


ALTER TABLE `jeepaydb`.`t_pay_order`
CHANGE COLUMN `ext_param` `ext_param` VARCHAR(256) NULL DEFAULT NULL COMMENT '商户扩展参数' ;


ALTER TABLE `jeepaydb`.`t_order_statistics_dept`
ADD COLUMN `remark` VARCHAR(512) NULL DEFAULT '' COMMENT '备注' AFTER `created_at`;