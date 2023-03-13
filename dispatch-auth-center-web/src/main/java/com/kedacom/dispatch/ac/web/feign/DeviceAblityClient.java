package com.kedacom.dispatch.ac.web.feign;

import com.kedacom.avcs.groupmanagement.GroupClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Description: 设备能接口
 */
@FeignClient(name = "deviceAblityClient", url = "${avcs.secretkey.callback.url: http://192.168.9.3:8090}",
        configuration = GroupClientConfiguration.class)
public interface DeviceAblityClient {

    // 清除设备能力缓存的授权信息
    @DeleteMapping("/abilityClient/deleteKeys")
    void deleteKeys(@RequestBody List<String> ids);
}
