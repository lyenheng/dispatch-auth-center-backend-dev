package com.kedacom.dispatch.ac.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 追加到url后面的参数（设备权限服务动态参数方式查询）
 * @Auther: liuyanhui
 * @Date: 2022/9/5 13:22
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDeviceQueryUrlParamDTO {

    // 是否过滤空分组
    private String filterEmptyStatus;
}
