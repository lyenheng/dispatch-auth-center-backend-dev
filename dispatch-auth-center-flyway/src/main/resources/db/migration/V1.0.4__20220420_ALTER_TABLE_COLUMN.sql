ALTER TABLE `tmp_r_mem`
    CHANGE COLUMN `r_id` `role_id`  varchar(255) CHARACTER SET utf8 NOT NULL COMMENT '用户角色' AFTER `mem_ty`;

ALTER TABLE `tmp_a_mem`
    CHANGE COLUMN `a_id` `api_id` varchar(255) CHARACTER SET utf8 NOT NULL AFTER `mem_ty`