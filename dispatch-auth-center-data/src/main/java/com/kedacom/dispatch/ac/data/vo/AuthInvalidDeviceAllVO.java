package com.kedacom.dispatch.ac.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/05/26/ 14:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthInvalidDeviceAllVO {
    private List<String> roleIds;
    // 新有效期
    private String newValidDate;

    private List<String> deviceTypes;

    private String deviceGroupId;

    private String deviceName;

    private String gbid;
}
