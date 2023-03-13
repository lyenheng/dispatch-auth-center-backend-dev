package com.kedacom.dispatch.ac.web.controller;

import com.kedacom.ctsp.web.controller.message.ResponseMessage;
import com.kedacom.dispatch.ac.data.dto.CodeInfoDTO;
import com.kedacom.dispatch.ac.web.service.CodeInfoService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenyang
 * @date 2022/3/9 13:40
 */
@RestController
@RequestMapping("/check")
@Slf4j
public class AuthCheckController {
    @ApiOperation("探针接口")
    @GetMapping("/version")
    public ResponseMessage<CodeInfoDTO> getVersion() {
        return ResponseMessage.ok();
    }
}
