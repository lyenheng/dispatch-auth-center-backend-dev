package com.kedacom.dispatch.ac.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/08/23/ 10:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceVo {
    private String deviceTypes;
    private Integer pageNo;
    private Integer pageSize;
    private String deviceGroupId;
    private String deviceName;
    private String gbid;
}
