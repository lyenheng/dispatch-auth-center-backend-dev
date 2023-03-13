package com.kedacom.dispatch.ac.web.feign;

import com.kedacom.ctsp.web.controller.message.ResponseMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author chenyang
 * @date 2022/3/11 13:26
 */
@FeignClient(name = "media-feign-client", url = "${auth.center.mediaClientUrl: http://192.168.9.249:8085}")
public interface NewMediaClient {

    @PostMapping("/media/cddc")
    ResponseMessage<Integer> getIsUsedConcurrent();
}
