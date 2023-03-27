
ALTER TABLE `jeepaydb`.`t_order_statistics_dept`
ADD COLUMN `parent_id` VARCHAR(50) NULL DEFAULT '' COMMENT '部门父id' AFTER `mch_name`;

ALTER TABLE `jeepaydb`.`t_order_statistics_dept`
ADD COLUMN `parent_name` VARCHAR(50) NULL DEFAULT '' COMMENT '父部门名称' AFTER `parent_id`;

ALTER TABLE `jeepaydb`.`t_order_statistics_dept`
ADD COLUMN `dept_id` VARCHAR(50) NULL DEFAULT '' COMMENT '部门id' AFTER `parent_name`;

ALTER TABLE `jeepaydb`.`t_order_statistics_dept`
ADD COLUMN `ext_type` VARCHAR(30) NULL DEFAULT '' COMMENT '标识位' AFTER `dept_name`;