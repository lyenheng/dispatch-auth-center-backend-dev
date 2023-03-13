package com.kedacom.dispatch.ac.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/06/07/ 18:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypeVO {
    private String deviceType;
    private String deviceTypeName;
    private String category;
    private Boolean isSelected;
    
}
