package com.kedacom.dispatch.ac.data.dto;

import lombok.Data;

/**
 * @author chenyang
 * @date 2021/11/17 19:17
 */
@Data
public class UsedDeviceAmountDTO {
    private String deviceType;
    /**
     * 已使用数量
     */
    private Integer inUsedAmount;
    /**
     * 剩余数量
     */
    private Integer unUsedAmount;
}
