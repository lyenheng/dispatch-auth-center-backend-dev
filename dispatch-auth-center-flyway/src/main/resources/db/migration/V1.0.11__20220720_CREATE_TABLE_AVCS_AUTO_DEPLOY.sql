DROP TABLE IF EXISTS `avcs_auto_deploy`;
CREATE TABLE `avcs_auto_deploy` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID 主键',
                                      `project_secret_key` varchar(100) NULL COMMENT '秘钥',
                                      `created_by` varchar(255) DEFAULT NULL,
                                      `created_time` datetime DEFAULT NULL,
                                      `updated_by` varchar(255) DEFAULT NULL,
                                      `updated_time` datetime DEFAULT NULL,
                                      `version` bigint(20) DEFAULT NULL,
                                      PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET=utf8mb4;