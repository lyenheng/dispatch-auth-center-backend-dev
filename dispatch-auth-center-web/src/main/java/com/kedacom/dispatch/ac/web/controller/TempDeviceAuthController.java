package com.kedacom.dispatch.ac.web.controller;


import cn.hutool.json.JSONObject;
import com.kedacom.ctsp.web.controller.message.ResponseMessage;
import com.kedacom.dispatch.ac.data.dto.AmenListDTO;
import com.kedacom.dispatch.ac.data.vo.AMemVO;
import com.kedacom.dispatch.ac.data.vo.MemVO;
import com.kedacom.dispatch.ac.data.vo.RMemVO;
import com.kedacom.dispatch.ac.web.service.AMemService;
import com.kedacom.dispatch.ac.web.service.RMemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author : liuyue
 * @Description :
 * @date : 2021/11/8 17:18
 */
@RestController
@Slf4j
@RequestMapping("/tempDeviceAuth")
@Api(tags = "Dispatch-auth-center", description = "临时设备授权")
public class TempDeviceAuthController {

    @Autowired
    private AMemService aMemService;

    @Autowired
    private RMemService rMemService;

    @ApiOperation("根据APIKey授权设备")
    @PostMapping("/aId")
    public ResponseMessage<List<AMemVO>> addAMem(@RequestBody AmenListDTO aMemVOList) {
        return ResponseMessage.ok(aMemService.addAMem(aMemVOList.getAMemVOList(),aMemVOList.getValiDate()));
    }

    @ApiOperation("查询所有APIKey授权设备")
    @GetMapping("/searchAllAMem")
    public ResponseMessage<JSONObject> searchAMem() {
        Map<String, List<MemVO>> stringListMap = aMemService.searchAllAMem();
        JSONObject json = new JSONObject(stringListMap);
        return ResponseMessage.ok(json);
    }

    @ApiOperation("根据APIKey删除记录")
    @DeleteMapping("/deleteApiKeys")
    public ResponseMessage deleteApiKeys(@RequestBody List<String> apiKeys){
        aMemService.deleteByA(apiKeys);
        return ResponseMessage.ok("删除成功！");
    }

    @ApiOperation("根据用户角色授权设备")
    @PostMapping("/rId")
    public ResponseMessage<List<RMemVO>> addRMem(@RequestBody List<RMemVO> rMemVOList) {
        return ResponseMessage.ok(rMemService.addRMem(rMemVOList));
    }

    @ApiOperation("根据角色查询授权设备")
    @PostMapping("/searchMemByR")
    public ResponseMessage<JSONObject> searchMemByR(@RequestBody List<String> roles) {
        Map<String, List<MemVO>> stringListMap = rMemService.searchMemByR(roles);
        JSONObject json = new JSONObject(stringListMap);
        return ResponseMessage.ok(json);
    }

    @ApiOperation("根据用户角色删除记录")
    @DeleteMapping("/deleteRole")
    public ResponseMessage deleteRoleId(@RequestBody List<String> roleIds){
        rMemService.deleteByR(roleIds);
        return ResponseMessage.ok("删除成功！");
    }

}
