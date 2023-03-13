CREATE TABLE `avcs_sms_in`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID 主键',
    `meet_plan_name`    varchar(100)  DEFAULT NULL COMMENT '预案名称',
    `dispatch_name`     varchar(100)  DEFAULT NULL COMMENT '调度名称 （默认预案名称+时间戳）',
    `start_time`        datetime      DEFAULT NULL COMMENT '调度开始时间',
    `end_time`          datetime      DEFAULT NULL COMMENT '调度结束时间',
    `group_id`          varchar(255)  DEFAULT NULL COMMENT '调度组ID',
    `record_start_time` datetime      DEFAULT NULL COMMENT '第一次录像开启时间',
    `record_end_time`   datetime      DEFAULT NULL COMMENT '最后一次录像关闭时间',
    `cr_storage_id`     varchar(255)  DEFAULT NULL COMMENT '录像ID',
    `request_id`        varchar(1000) DEFAULT NULL COMMENT '请求的唯一标识',
    `record_id`         varchar(255) NOT NULL COMMENT '会议录像接口返回的唯一标识',
    `record_type`       varchar(255)  DEFAULT NULL COMMENT '录像类型',
    `is_deleted`        tinyint(4) DEFAULT NULL COMMENT '是否已被逻辑删除',
    `created_by`        varchar(255)  DEFAULT NULL,
    `created_time`      datetime      DEFAULT NULL,
    `updated_by`        varchar(255)  DEFAULT NULL,
    `updated_time`      datetime      DEFAULT NULL,
    `version`           bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;