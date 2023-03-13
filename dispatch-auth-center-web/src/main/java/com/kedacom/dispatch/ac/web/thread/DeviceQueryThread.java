package com.kedacom.dispatch.ac.web.thread;

import com.kedacom.adc.common.dto.device.RaDeviceDTO;
import com.kedacom.ctsp.web.entity.page.PagerResult;
import com.kedacom.dispatch.ac.data.dto.AuthDeviceQueryBodyDTO;
import com.kedacom.dispatch.ac.web.service.AdcService;
import com.kedacom.dispatch.common.data.dto.adc.SimpleAdcDeviceDTO;
import com.kedacom.dispatch.common.resource.auth.feign.req.HeaderReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @Description: 设备权限：设备查询线程
 * @Auther: liuyanhui
 * @Date: 2022/08/10/ 15:08
 */
@Slf4j
public class DeviceQueryThread implements Callable<List<SimpleAdcDeviceDTO>> {

    private AdcService adcService;
    private RestTemplate restTemplate;
    private HeaderReq headerReq;
    private AuthDeviceQueryBodyDTO bodyDTO;
    private Boolean isFilterRoles;
    private String newValidDate;
    public DeviceQueryThread(AdcService adcService, RestTemplate restTemplate, HeaderReq headerReq, AuthDeviceQueryBodyDTO bodyDTO,Boolean isFilterRoles,String newValidDate) {
        this.adcService = adcService;
        this.restTemplate = restTemplate;
        this.headerReq = headerReq;
        this.bodyDTO = bodyDTO;
        this.isFilterRoles = isFilterRoles;
        this.newValidDate = newValidDate;
    }

    @Override
    public List<SimpleAdcDeviceDTO> call(){
        String url = adcService.createUrl();
        PagerResult<SimpleAdcDeviceDTO> pagerResult = adcService.queryDevices(url,restTemplate,headerReq,bodyDTO,isFilterRoles,newValidDate);
        if(null == pagerResult){
            return null;
        }
        return pagerResult.getData();
    }
}
