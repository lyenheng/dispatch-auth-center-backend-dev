DROP TABLE IF EXISTS `avcs_device_type`;
CREATE TABLE `avcs_device_type`  (
                                     `id` bigint(20)     NOT NULL AUTO_INCREMENT,
                                     `device_type`       varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                     `device_type_name`  varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                     `category`          varchar(1) NULL NOT NULL COMMENT '0:监控,1:会议终端,2:电话,3:数字集群,4:APP用户,5:其他',
                                     `is_edit`            varchar(1)  DEFAULT '0' COMMENT '是否可编辑 0:是,1:否',
                                     `created_by`        varchar(255)  DEFAULT NULL,
                                     `created_time`      datetime      DEFAULT NULL,
                                     `updated_by`        varchar(255)  DEFAULT NULL,
                                     `updated_time`      datetime      DEFAULT NULL,
                                     `version`           bigint(20) DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `avcs_device_type` VALUES (1, 'IPC','网络摄像机', 0,'1',null,null,null,null,NULL);
INSERT INTO `avcs_device_type` VALUES (2, 'BWC','执法记录仪', 0,'1',null,null,null,null,NULL);
INSERT INTO `avcs_device_type` VALUES (3, 'Bodyworn','单兵', 0,'1',null,null,null,null,NULL);
INSERT INTO `avcs_device_type` VALUES (4, 'UAV','无人机', 0,'1',null,null,null,null,NULL);
INSERT INTO `avcs_device_type` VALUES (5, 'MT', '会议终端',1,'1',null,null,null,null,NULL);
INSERT INTO `avcs_device_type` VALUES (6, 'TEL','电话', 2,'1',null,null,null,null,NULL);
INSERT INTO `avcs_device_type` VALUES (7,'PTT','对讲机', 3,'1',null,null,null,null,NULL);
INSERT INTO `avcs_device_type` VALUES (8,'APP','视讯通', 4,'1',null,null,null,null,NULL);
INSERT INTO `avcs_device_type` VALUES (9, 'Decoder','解码器', 5,'1',null,null,null,null,NULL);
INSERT INTO `avcs_device_type` VALUES (10, 'Location','定位设备', 5,'1',null,null,null,null,NULL);
INSERT INTO `avcs_device_type` VALUES (11, 'MSP','拼接器', 5,'1',null,null,null,null,NULL);
INSERT INTO `avcs_device_type` VALUES (12, 'Encoder','编码器', 5,'1',null,null,null,null,NULL);