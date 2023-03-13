package com.kedacom.dispatch.ac.web.feign;

import com.kedacom.ctsp.web.controller.message.ResponseMessage;
import com.kedacom.dispatch.ac.data.dto.ApiKeyInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author chenyang
 * @date 2021/11/25 15:18
 */
@FeignClient(name = "kiop-feign-client", url = "${dispatch.common.kiop.url:https://dispatch.testdolphin.com/kiop-admin}")
public interface KiopFeignClient {

    @GetMapping("/user/apikey/list")
    ResponseMessage<List<ApiKeyInfoDTO>> listApiKey();
}
