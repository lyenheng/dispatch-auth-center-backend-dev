package com.kedacom.dispatch.ac.web.service;

import com.kedacom.dispatch.ac.data.entity.AvcsDeviceType;
import com.kedacom.dispatch.ac.data.vo.DeviceTypeReqVO;
import com.kedacom.dispatch.ac.data.vo.DeviceTypeResVO;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/06/07/ 18:31
 */
public interface AvcsDeviceTypeService {

    List<AvcsDeviceType> getDeviceTypes();

    void add(DeviceTypeReqVO deviceTypeReqVO);
}
