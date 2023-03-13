package com.kedacom.dispatch.ac.data.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author chenyang
 * @date 2022/3/9 13:41
 */
@Data
public class CodeInfoDTO {
    /**
     * 融合通信容量版本
     */
    private String desc;
    /**
     * 有效期
     */
    private Date expirationTime;
    /**
     * 版本信息
     */
    private Integer versionInfo;
    /**
     * 监控数
     */
    //监控数
    private Integer ipcAmount = 0;
    /**
     * 会议终端数
     */
    private Integer terminalAmount = 0;
    /**
     * 电话数
     */
    private Integer telephoneAmount = 0;
    /**
     * 数字集群
     */
    private Integer pttAmount = 0;
    /**
     * app数
     */
    private Integer appAmount = 0;
    /**
     * 其他数量
     */
    private Integer otherAmount = 0;
    /**
     *并发量
     */
    private Integer concurrentAmount = 0;

    /**
     * 已使用并发量
     */
    private Integer isUsedConcurrentAmount = 0;
}
