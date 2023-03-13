package com.kedacom.dispatch.ac.data.dto;

import com.kedacom.adc.common.dto.device.ValidDateItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/05/10/ 16:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DeviceDTO {
    private String id;

    private String deviceName;

    private String deviceType;

//    private String gbid;

    private String groupId;

    private String validDate;

//    private String groupName;
//
    private List<String> roleIds;

    private Boolean hasValidDate;
    // "设备过期时间集合"
    private List<ValidDateItemDTO> validDateItems;
}
