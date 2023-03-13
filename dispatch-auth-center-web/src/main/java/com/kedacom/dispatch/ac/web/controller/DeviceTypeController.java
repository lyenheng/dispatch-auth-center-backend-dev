package com.kedacom.dispatch.ac.web.controller;

import com.kedacom.ctsp.web.controller.message.ResponseMessage;
import com.kedacom.dispatch.ac.data.entity.AvcsDeviceType;
import com.kedacom.dispatch.ac.data.vo.DeviceTypeReqVO;
import com.kedacom.dispatch.ac.web.service.AvcsDeviceTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/06/07/ 17:50
 */
@RestController
@Slf4j
@RequestMapping("/deviceType")
@Api(tags = "授权设备类型管理")
public class DeviceTypeController {

    @Autowired
    private AvcsDeviceTypeService avcsDeviceTypeService;

    @ApiOperation("添加设备类型")
    @PostMapping("/add")
    public ResponseMessage<String> add(@RequestBody DeviceTypeReqVO deviceTypeReqVO) {
        avcsDeviceTypeService.add(deviceTypeReqVO);
        return ResponseMessage.ok();
    }

    @ApiOperation("获取设备类型")
    @GetMapping("/getDeviceTypes")
    public ResponseMessage<List<AvcsDeviceType>> getDeviceTypes() {
        return ResponseMessage.ok(avcsDeviceTypeService.getDeviceTypes());
    }
}
