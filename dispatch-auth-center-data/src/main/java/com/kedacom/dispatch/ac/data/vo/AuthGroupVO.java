package com.kedacom.dispatch.ac.data.vo;

import com.kedacom.dispatch.common.data.dto.adc.DeviceGroupDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 按分组授权vo
 * @Auther: liuyanhui
 * @Date: 2022/5/12 14:33
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthGroupVO {
    private List<String> groupIds;
    private List<String> roleIds;
    private List<String> deviceTypes;
    // 新有效期
    private String newValidDate;
    private List<DeviceGroupDTO> deviceGroupList;


    private Boolean isDevolve = false;
}
