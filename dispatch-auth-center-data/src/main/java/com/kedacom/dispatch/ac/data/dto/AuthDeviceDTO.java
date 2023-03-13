package com.kedacom.dispatch.ac.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chenyang
 * @date 2021/11/17 19:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDeviceDTO {
    private List<DeviceDTO> devices;
     private List<String> roleIds;
    // 新有效期
    private String newValidDate;
    // 旧的有效期
    private String oldValidDate;

    private List<String> deviceTypes;

    private String deviceGroupId;

    private String deviceName;

    private String gbid;

    private List<String> groupIds;
}
