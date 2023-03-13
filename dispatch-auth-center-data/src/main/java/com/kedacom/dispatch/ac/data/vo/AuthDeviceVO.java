package com.kedacom.dispatch.ac.data.vo;

import com.kedacom.dispatch.ac.data.dto.DeviceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 按设备授权vo
 * @Auther: liuyanhui
 * @Date: 2022/5/12 14:33
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDeviceVO {
    private List<DeviceDTO> devices;
    private List<String> roleIds;
    // 新有效期
    private String newValidDate;

    private Boolean isDevolve = false;
}
