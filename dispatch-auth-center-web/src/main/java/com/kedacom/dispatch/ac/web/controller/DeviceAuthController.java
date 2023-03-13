package com.kedacom.dispatch.ac.web.controller;

import com.kedacom.ctsp.web.controller.message.ResponseMessage;
import com.kedacom.ctsp.web.entity.page.PagerResult;
import com.kedacom.dispatch.ac.data.dto.ApiKeyInfoDTO;
import com.kedacom.dispatch.ac.data.dto.AuthDeviceDTO;
import com.kedacom.dispatch.ac.data.dto.AuthDevicePageDTO;
import com.kedacom.dispatch.ac.data.dto.AuthDeviceTypeDTO;
import com.kedacom.dispatch.ac.data.dto.AuthGroupDTO;
import com.kedacom.dispatch.ac.data.dto.CancelAuthDTO;
import com.kedacom.dispatch.ac.data.dto.QueryNumByGroupDTO;
import com.kedacom.dispatch.ac.data.dto.RoleDateDTO;
import com.kedacom.dispatch.ac.data.dto.UsedDeviceAmountDTO;
import com.kedacom.dispatch.ac.data.vo.AuthDeviceVO;
import com.kedacom.dispatch.ac.data.vo.AuthGroupVO;
import com.kedacom.dispatch.ac.data.vo.AuthInvalidDeviceAllVO;
import com.kedacom.dispatch.ac.data.vo.AuthInvalidDeviceVO;
import com.kedacom.dispatch.ac.web.service.DeviceAuthService;
import com.kedacom.dispatch.common.data.dto.adc.SimpleAdcDeviceDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author chenyang
 * @date 2021/11/17 18:40
 * @version 2.0
 */
@RestController
@Slf4j
@RequestMapping("/deviceAuth")
@Api(tags = "授权中心")
public class DeviceAuthController {

    @Autowired
    private DeviceAuthService deviceAuthService;

    @ApiOperation("根据设备授权")
    @PostMapping("/authByDevice")
    public ResponseMessage<String> authByDevice(@RequestBody AuthDeviceVO authDeviceVO) {
        AuthDeviceDTO authDeviceDTO = new AuthDeviceDTO();
        BeanUtils.copyProperties(authDeviceVO,authDeviceDTO);
        deviceAuthService.authByDevice(authDeviceDTO);
        return ResponseMessage.ok();
    }

    @ApiOperation("根据分组授权")
    @PostMapping("/authByGroup")
    public ResponseMessage<String> authByGroup(@RequestBody AuthGroupVO authGroupVO) {
        AuthGroupDTO authGroupDTO = new AuthGroupDTO();
        BeanUtils.copyProperties(authGroupVO,authGroupDTO);
        deviceAuthService.authByGroup(authGroupDTO);
        return ResponseMessage.ok();
    }
//    @ApiOperation("根据分组取消授权")
//    @PostMapping("/cancelAuthByGroup")
//    public ResponseMessage<String> cancelAuthByGroup(@RequestBody CancelAuthGroupVO cancelAuthGroupVO) {
//        AuthGroupDTO authGroupDTO = new AuthGroupDTO();
//        BeanUtils.copyProperties(cancelAuthGroupVO,authGroupDTO);
//        authGroupDTO.setNewValidDate(cancelAuthGroupVO.getValidDate());
//        deviceAuthService.cancelAuthByGroup(authGroupDTO);
//        return ResponseMessage.ok();
//    }
    // 1
    @ApiOperation("修改设备授权时间")
    @PostMapping("/dateChg")
    public ResponseMessage<String> dateChg(@RequestBody AuthDeviceDTO authDeviceDTO){
        deviceAuthService.updateAuthByDevice(authDeviceDTO);
        return ResponseMessage.ok();
    }

    @ApiOperation("修改角色下全部设备授权时间")
    @PostMapping("/dateChgAll")
    public ResponseMessage<String> dateChgAll(@RequestBody AuthDeviceDTO authDeviceDTO){
        deviceAuthService.updateAuthByAllDevice(authDeviceDTO);
        return ResponseMessage.ok();
    }

    @ApiOperation("过期设备批量转授权")
    @PostMapping("/authByInvalidDevice")
    public ResponseMessage<String> authByInvalidDevice(@RequestBody AuthInvalidDeviceVO authInvalidDeviceVO){
        deviceAuthService.authByInvalidDevice(authInvalidDeviceVO);
        return ResponseMessage.ok();
    }
    @ApiOperation("过期设备全部转授权")
    @PostMapping("/authByInvalidDeviceAll")
    public ResponseMessage<String> authByInvalidDeviceAll(@RequestBody AuthInvalidDeviceAllVO authInvalidDeviceAllVO){
        deviceAuthService.authByInvalidDeviceAll(authInvalidDeviceAllVO);
        return ResponseMessage.ok();
    }
    @ApiOperation("取消授权")
    @PostMapping("/cancelDeviceAuth")
    public ResponseMessage<String> cancelDeviceAuth(@RequestBody AuthDeviceDTO authDeviceDTO) {
        deviceAuthService.cancelDeviceAuth(authDeviceDTO);
        return ResponseMessage.ok();
    }

    @ApiOperation("根据设备类型取消授权")
    @PostMapping("/cancelDeviceAuthByType")
    public ResponseMessage<String> cancelDeviceAuthByType(@RequestBody CancelAuthDTO authDeviceDTO) {
        deviceAuthService.cancelDeviceAuthByType(authDeviceDTO);
        return ResponseMessage.ok();
    }

    @ApiOperation("根据授权时间查询已使用的设备数量")
    @GetMapping("/getUsedDeviceAmountByExpTime/{validDate}")
    public ResponseMessage<List<UsedDeviceAmountDTO>> getUsedDeviceAmountByExpTime(@PathVariable("validDate") String validDate) {
        return ResponseMessage.ok(deviceAuthService.getUsedDeviceAmountByExpTime(validDate));
    }

    @ApiOperation("获取所有设备类型")
    @GetMapping("/getAllDeviceType")
    public ResponseMessage<List<AuthDeviceTypeDTO>> getAllDeviceTypeList() {
        return ResponseMessage.ok(deviceAuthService.getAllDeviceTypeList());
    }

    @ApiOperation("分页查询已授权设备")
    @PostMapping("/getAuthorizedDevice")
    public ResponseMessage<PagerResult<SimpleAdcDeviceDTO>> getAuthorizedDevice(@RequestBody AuthDevicePageDTO authDevicePageDTO) {
        return ResponseMessage.ok(deviceAuthService.getAuthorizedDevice(authDevicePageDTO));
    }

    @ApiOperation("查询所有的apikey")
    @GetMapping("/getAllApiKey")
    public ResponseMessage<List<ApiKeyInfoDTO>> getAllApiKey() {
        return ResponseMessage.ok(deviceAuthService.getAllApiKey());
    }
    @ApiOperation("查询所有的角色列表")
    @GetMapping("/getAllRoles")
    public ResponseMessage<List<String>> getAllRoles() {
        return ResponseMessage.ok(deviceAuthService.getAllRoleIds());
    }

    @ApiOperation("刷新页面查询操作进度")
    @GetMapping("/getOperationProgress")
    public ResponseMessage<String> getOperationProgress() {
        return ResponseMessage.ok(deviceAuthService.getOperationProgress());
    }

    @ApiOperation("查询分组下的设备数量")
    @PostMapping("/getDeviceNumByGroup")
    public ResponseMessage<Map<String, Long>> getDeviceNumByGroup(@RequestBody QueryNumByGroupDTO queryNumByGroupDTO){
        return ResponseMessage.ok(deviceAuthService.getDeviceNumByGroup(queryNumByGroupDTO));
    }

    @ApiOperation("分页查询已过期设备")
    @PostMapping("/getExpiredDevice")
    public ResponseMessage<PagerResult<SimpleAdcDeviceDTO>> getExpiredDevice(@RequestBody AuthDevicePageDTO authDevicePageDTO){
        return ResponseMessage.ok(deviceAuthService.getDevicesByExpDate(authDevicePageDTO));
    }

    @ApiOperation("查询已过期设备的总数,0:平台  1：轻服务  2：所有")
    @GetMapping("/getExpiredTotal/{paltformType}")
    public ResponseMessage<Long> getExpiredTotal(@PathVariable("paltformType")Integer paltformType){
        return ResponseMessage.ok(deviceAuthService.getExpiredTotal(paltformType));
    }

    @ApiOperation("注销所有设备授权")
    @DeleteMapping("/cancelDeviceAuthAll")
    public ResponseMessage<String> cancelDeviceAuthAll(){
        deviceAuthService.cancelDeviceAuthAll();
        return ResponseMessage.ok();
    }

    @ApiOperation("获取授权时间列表")
    @GetMapping("/getAuthDates")
    public ResponseMessage<List<String>> getAuthDates(){
        return ResponseMessage.ok(deviceAuthService.getAuthDates());
    }


    @ApiOperation("根据授权日期取消授权")
    @DeleteMapping("/cancelDeviceAuthByDate/{date}")
    public ResponseMessage<String> cancelDeviceAuthByDate(@PathVariable("date") String date){
        String dt = date.replaceAll("[-+,:+,\\s]", "");
        deviceAuthService.cancelDeviceAuthByDate(dt);
        return ResponseMessage.ok();
    }

    @ApiOperation("根据授权日期和角色取消授权")
    @DeleteMapping("/cancelDeviceAuthByDateAnRoles")
    public ResponseMessage<String> cancelDeviceAuthByDateAnRoles(@RequestBody RoleDateDTO roleDateDTO){
        deviceAuthService.cancelDeviceAuthByDateAnRoles(roleDateDTO);
        return ResponseMessage.ok();
    }
//    @ApiOperation("查询未授权设备")
//    @DeleteMapping("/getUnAuthDevices")
//    public ResponseMessage<PagerResult<RaDeviceDTO>> getUnAuthDevices(UnauthDevicesVO unauthDevicesVO){
//        PagerResult<RaDeviceDTO> unAuthDevices = deviceAuthService.getUnAuthDevices(unauthDevicesVO);
//        return ResponseMessage.ok(unAuthDevices);
//    }

//    @ApiOperation("查询当前登录人已授权设备（超管可见全部）")
//    @DeleteMapping("/getCurrentUserAuthDevice")
//    public ResponseMessage<PagerResult<RaDeviceDTO>> getCurrentUserAuthDevice(UnauthDevicesVO unauthDevicesVO){
//        PagerResult<RaDeviceDTO> unAuthDevices = deviceAuthService.getCurrentUserAuthDevice(unauthDevicesVO);
//        return ResponseMessage.ok(unAuthDevices);
//    }

//    @Autowired
//    DeviceAblityClient deviceAblityClient;
//
//    @ApiOperation("获取授权时间列表")
//    @DeleteMapping("/TEST")
//    public ResponseMessage<List<String>> TEST(){
//        deviceAblityClient.deleteKeys(new ArrayList<>());
//        return ResponseMessage.ok();
//    }

}
