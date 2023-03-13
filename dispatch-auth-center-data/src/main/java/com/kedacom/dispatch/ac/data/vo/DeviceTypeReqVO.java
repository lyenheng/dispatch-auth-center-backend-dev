package com.kedacom.dispatch.ac.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/06/07/ 18:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypeReqVO {
   List<DeviceTypeVO> deviceTypeVOS;
}
