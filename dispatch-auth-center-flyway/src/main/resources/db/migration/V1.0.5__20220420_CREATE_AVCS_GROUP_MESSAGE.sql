DROP TABLE IF EXISTS `avcs_group_device`;
CREATE TABLE `avcs_group_device`  (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `group_id` varbinary(255) NULL DEFAULT NULL COMMENT '调度组id',
                                      `media_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '媒体类型',
                                      `device_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备id',
                                      `device_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备类型',
                                      `device_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备名称',
                                      `gb_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '国标id',
                                      `vline_account` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '视信通账号',
                                      `created_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `created_time` datetime(0) NULL DEFAULT NULL,
                                      `updated_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `updated_time` datetime(0) NULL DEFAULT NULL,
                                      `version` bigint NULL DEFAULT NULL,
                                      `deleted` bit(1) NULL DEFAULT b'0' COMMENT '是否删除',
                                      `end_time` datetime(0) NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `avcs_group_info`;
CREATE TABLE `avcs_group_info`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID 主键',
                                    `group_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度组id',
                                    `creator_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者id',
                                    `creator_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者名称',
                                    `api_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'kiop调用的key',
                                    `group_created_time` datetime(0) NULL DEFAULT NULL COMMENT '调度组创建时间',
                                    `created_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                    `created_time` datetime(0) NULL DEFAULT NULL,
                                    `updated_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                    `updated_time` datetime(0) NULL DEFAULT NULL,
                                    `version` bigint NULL DEFAULT NULL,
                                    `dispatch_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '调度历史id',
                                    `group_delete_time` datetime(0) NULL DEFAULT NULL COMMENT '调度组删除时间',
                                    `secret_chat_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密信群组id',
                                    `deleted` bit(1) NULL DEFAULT b'0' COMMENT '是否删除',
                                    `meeting_type` tinyint NULL DEFAULT NULL COMMENT '会议类型',
                                    `relation_id` bigint NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `avcs_topic_message_log`;
CREATE TABLE `avcs_topic_message_log`  (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID 主键',
                                           `component_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组件ID',
                                           `module_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模块名',
                                           `topic` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息topic',
                                           `send_receive` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息topic-收发方式',
                                           `message` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Device ID - 设备ID',
                                           `created_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者',
                                           `updated_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者',
                                           `created_time` datetime(0) NOT NULL COMMENT '创建时间',
                                           `updated_time` datetime(0) NOT NULL COMMENT '更新时间',
                                           `version` bigint NOT NULL DEFAULT 1 COMMENT '版本控制',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `group_history_info`;
CREATE TABLE `group_history_info`  (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `mp_dispatch_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '预案调度名称',
                                       `meet_room_id` bigint NOT NULL COMMENT '会场id',
                                       `meet_room_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会场名称',
                                       `meet_plan_id` bigint NOT NULL COMMENT '预案id',
                                       `meet_plan_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '预案名称',
                                       `start_time` datetime(0) NOT NULL COMMENT '开始时间',
                                       `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
                                       `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                       `created_time` datetime(0) NOT NULL,
                                       `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                       `updated_time` datetime(0) NOT NULL,
                                       `version` bigint NOT NULL,
                                       `department_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门编码',
                                       `department_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
                                       `dispatch_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                       `meeting_type` tinyint NULL DEFAULT NULL COMMENT '会议类型',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `vline_device_info`;
CREATE TABLE `vline_device_info`  (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID 主键',
                                      `device_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '视信通id',
                                      `group_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密信组id',
                                      `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
                                      `created_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `created_time` datetime(0) NULL DEFAULT NULL,
                                      `updated_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `updated_time` datetime(0) NULL DEFAULT NULL,
                                      `version` bigint NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `vline_group_info`;
CREATE TABLE `vline_group_info`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID 主键',
                                     `group_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密信组id',
                                     `dispatch_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调度历史id',
                                     `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
                                     `created_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                     `created_time` datetime(0) NULL DEFAULT NULL,
                                     `updated_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                     `updated_time` datetime(0) NULL DEFAULT NULL,
                                     `version` bigint NULL DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;