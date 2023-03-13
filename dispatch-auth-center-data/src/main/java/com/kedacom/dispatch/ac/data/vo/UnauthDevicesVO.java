package com.kedacom.dispatch.ac.data.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/08/25/ 10:35
 */
@Data
public class UnauthDevicesVO {
    private List<String> roleIds;
    private String deviceName;
    private List<String> deviceTypes;
    private String gbid;
    private String deviceGroupId;
    private Integer pageNo;
    private Integer pageSize;
}
