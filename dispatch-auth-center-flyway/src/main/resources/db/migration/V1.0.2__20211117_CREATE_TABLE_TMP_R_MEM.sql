CREATE TABLE `tmp_r_mem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mem_id` varchar(255) NOT NULL COMMENT '设备id',
  `mem_ty` varchar(255) NOT NULL COMMENT '设备类型',
  `r_id` varchar(255) NOT NULL COMMENT '用户角色',
  `created_by` varchar(255) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4;