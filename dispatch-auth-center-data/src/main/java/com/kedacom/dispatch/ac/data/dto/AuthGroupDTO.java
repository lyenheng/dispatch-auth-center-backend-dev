package com.kedacom.dispatch.ac.data.dto;

import com.kedacom.dispatch.common.data.dto.adc.DeviceGroupDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author chenyang
 * @date 2021/11/17 19:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthGroupDTO {
    private List<String> groupIds;
    private List<String> roleIds;
    private List<String> deviceTypes;
    // 新有效期
    private String newValidDate;
    private List<DeviceDTO> devices;
    private List<DeviceGroupDTO> deviceGroupList;

    private Boolean isDevolve = false;
}
