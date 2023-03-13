package com.kedacom.dispatch.ac.web.feign;

import com.kedacom.ctsp.authz.entity.AuthRole;
import com.kedacom.ctsp.web.controller.message.ResponseMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Description: 统一权限接口
 * @Auther: liuyanhui
 * @Date: 2022/5/17 18:26
 */

@FeignClient(name = "rabc-feign-client", url = "${dispatch.common.rabc.url:http://192.168.9.3：8092}")
public interface CommonRabcClient {

    @GetMapping("/v2/rbac/project/valid_roles")
    ResponseMessage<List<AuthRole>> getRabcRoles();
}
