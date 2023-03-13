package com.kedacom.dispatch.ac.data.dto;

import lombok.Data;

import java.util.List;

/**
 * @author chenyang
 * @date 2021/11/24 14:08
 */
@Data
public class AuthDevicePageDTO {
    private List<String> roleIds;

    private String deviceName;

    private String gbid;

    private String deviceGroupId;

    private List<String> deviceTypes;

    private Integer pageSize = 25;

    private Integer pageNo = 0;

    private String validDate;

}
