DROP TABLE IF EXISTS `avcs_cache`;
CREATE TABLE `avcs_cache`  (
                               `key_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'keyId 主键',
                               `value` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'value',
                               `class_name` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'class_name',
                               PRIMARY KEY (`key_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '缓存备份' ROW_FORMAT = Dynamic;