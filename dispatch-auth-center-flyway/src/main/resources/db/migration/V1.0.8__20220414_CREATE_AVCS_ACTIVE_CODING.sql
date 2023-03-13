DROP TABLE IF EXISTS `avcs_active_coding`;
CREATE TABLE `avcs_active_coding` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID 主键',
                                      `project_name` varchar(100) NULL COMMENT '项目名称',
                                      `project_secret_key` varchar(100) NULL COMMENT '秘钥',
                                      `active_code` text NULL COMMENT '激活码',
                                      `version_info` int(2) default null COMMENT '版本信息  0-一体机版本  1-融合通信版本(标准版)  2-政法标准版  3-政法基础版',
                                      `state` int(4) default null COMMENT '是否启用，0-启用，1-禁用',
                                      `increment` text default null COMMENT '增量',
                                      `created_by` varchar(255) DEFAULT NULL,
                                      `created_time` datetime DEFAULT NULL,
                                      `updated_by` varchar(255) DEFAULT NULL,
                                      `updated_time` datetime DEFAULT NULL,
                                      `version` bigint(20) DEFAULT NULL,
                                      PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET=utf8mb4;