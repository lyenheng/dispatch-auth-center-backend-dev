package com.kedacom.dispatch.ac.web.service;

import com.kedacom.adc.common.dto.device.RaDeviceDTO;
import com.kedacom.ctsp.web.entity.page.PagerResult;
import com.kedacom.dispatch.ac.data.dto.AuthDeviceQueryBodyDTO;
import com.kedacom.dispatch.ac.data.dto.AuthDeviceQueryUrlParamDTO;
import com.kedacom.dispatch.common.data.dto.adc.SimpleAdcDeviceDTO;
import com.kedacom.dispatch.common.resource.auth.feign.req.HeaderReq;
import org.springframework.web.client.RestTemplate;

/**
 * @Description: 设备权限接口
 * @Auther: liuyanhui
 * @Date: 2022/08/11/ 13:49
 */
public interface AdcService {
    PagerResult<SimpleAdcDeviceDTO> queryDevices(String url, RestTemplate restTemplate, HeaderReq headerReq,
                                                 AuthDeviceQueryBodyDTO authDeviceQueryBodyDTO, Boolean isFilterRoles, String newValidDate);
    String createUrl();
//    StringBuilder getRootUrlBuilder(AuthDeviceQueryUrlParamDTO authDeviceQueryUrlParamDTO);
}
