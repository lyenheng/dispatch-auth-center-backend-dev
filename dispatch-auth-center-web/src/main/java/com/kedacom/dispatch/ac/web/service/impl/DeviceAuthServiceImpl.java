package com.kedacom.dispatch.ac.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.kedacom.adc.common.dto.device.RaDeviceDTO;
import com.kedacom.adc.common.dto.device.ValidDateItemDTO;
import com.kedacom.adc.common.vo.role.RoleValidDateUpdateReqVO;
import com.kedacom.adc.common.vo.role.ValidDateUpdateItemVO;
import com.kedacom.avcs.dispatch.annotation.AvcsServiceLockAno;
import com.kedacom.avcs.dispatch.cache.common.CacheService;
import com.kedacom.avcs.dispatch.manage.data.constants.ActiveCodingConstants;
import com.kedacom.avcs.dispatch.manage.data.dto.DetailDTO;
import com.kedacom.avcs.dispatch.secretkey.data.constants.ModelEnum;
import com.kedacom.avcs.dispatch.secretkey.data.dto.LatestFixedServerDTO;
import com.kedacom.avcs.dispatch.secretkey.data.dto.LatestModelDTO;
import com.kedacom.avcs.dispatch.secretkey.web.service.SecretKeyService;
import com.kedacom.avcs.dispatch.socket.websocket.service.WebSocketService;
import com.kedacom.ctsp.web.entity.page.PagerResult;
import com.kedacom.dispatch.ac.common.constant.DateConstant;
import com.kedacom.dispatch.ac.common.utils.DateUtils;
import com.kedacom.dispatch.ac.data.constants.AuthCenterConstant;
import com.kedacom.dispatch.ac.data.constants.AuthDeviceQueryBodyConstant;
import com.kedacom.dispatch.ac.data.constants.AuthTypeConstant;
import com.kedacom.dispatch.ac.data.constants.RedisKey;
import com.kedacom.dispatch.ac.data.dao.AvcsDeviceTypeDao;
import com.kedacom.dispatch.ac.data.dto.ApiKeyInfoDTO;
import com.kedacom.dispatch.ac.data.dto.AuthCenterMsg;
import com.kedacom.dispatch.ac.data.dto.AuthDeviceDTO;
import com.kedacom.dispatch.ac.data.dto.AuthDevicePageDTO;
import com.kedacom.dispatch.ac.data.dto.AuthDeviceQueryBodyDTO;
import com.kedacom.dispatch.ac.data.dto.AuthDeviceTypeDTO;
import com.kedacom.dispatch.ac.data.dto.AuthGroupDTO;
import com.kedacom.dispatch.ac.data.dto.AuthProcessDTO;
import com.kedacom.dispatch.ac.data.dto.CancelAuthDTO;
import com.kedacom.dispatch.ac.data.dto.DeviceDTO;
import com.kedacom.dispatch.ac.data.dto.InterfaceStatusDTO;
import com.kedacom.dispatch.ac.data.dto.QueryNumByGroupDTO;
import com.kedacom.dispatch.ac.data.dto.RoleDateDTO;
import com.kedacom.dispatch.ac.data.dto.UsedDeviceAmountDTO;
import com.kedacom.dispatch.ac.data.entity.AvcsDeviceType;
import com.kedacom.dispatch.ac.data.enums.DeviceCategoryEnum;
import com.kedacom.dispatch.ac.data.exception.AuthExceptionCode;
import com.kedacom.dispatch.ac.data.vo.AuthInvalidDeviceAllVO;
import com.kedacom.dispatch.ac.data.vo.AuthInvalidDeviceVO;
import com.kedacom.dispatch.ac.web.feign.DeviceAblityClient;
import com.kedacom.dispatch.ac.web.feign.KiopFeignClient;
import com.kedacom.dispatch.ac.web.service.AMemService;
import com.kedacom.dispatch.ac.web.service.AdcService;
import com.kedacom.dispatch.ac.web.service.DeviceAuthService;
import com.kedacom.dispatch.ac.web.thread.DeviceCountThread;
import com.kedacom.dispatch.ac.web.thread.DeviceQueryThread;
import com.kedacom.dispatch.common.adc.service.CommonAdcApiService;
import com.kedacom.dispatch.common.adc.util.RequestHeaderUtil;
import com.kedacom.dispatch.common.data.DispatchResponseMessage;
import com.kedacom.dispatch.common.data.constant.DeviceOperateTypeEnum;
import com.kedacom.dispatch.common.data.constant.DeviceQueryTypeEnum;
import com.kedacom.dispatch.common.data.constant.MemberTypeEnum;
import com.kedacom.dispatch.common.data.constant.RbacStatusEnum;
import com.kedacom.dispatch.common.data.dto.adc.DeviceGroupDTO;
import com.kedacom.dispatch.common.data.dto.adc.DeviceIdsAndNumParamDTO;
import com.kedacom.dispatch.common.data.dto.adc.DevicePageParamDTO;
import com.kedacom.dispatch.common.data.dto.adc.DevicesAndNumDTO;
import com.kedacom.dispatch.common.data.dto.adc.SimpleAdcDeviceDTO;
import com.kedacom.dispatch.common.data.dto.rbac.ProjectRoleDTO;
import com.kedacom.dispatch.common.data.dto.rbac.RoleQueryDTO;
import com.kedacom.dispatch.common.data.exception.CommonCodeMessage;
import com.kedacom.dispatch.common.data.exception.CommonException;
import com.kedacom.dispatch.common.data.exception.DispatchMessage;
import com.kedacom.dispatch.common.rbac.service.CommonRbacApiService;
import com.kedacom.dispatch.common.resource.auth.feign.ResourceAuthClient;
import com.kedacom.dispatch.common.resource.auth.feign.req.HeaderReq;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.kedacom.avcs.dispatch.secretkey.web.service.impl.SecretKeyServiceImpl.cacheLicenseMap;

/**
 * @author chenyang
 * @date 2021/11/17 19:45
 */
@Service
@Slf4j
public class DeviceAuthServiceImpl implements DeviceAuthService {

    @Autowired
    private CommonAdcApiService commonAdcApiService;

    @Autowired
    private CommonRbacApiService commonRbacApiService;

    @Autowired
    private SecretKeyService secretKeyService;

    @Autowired
    private AMemService aMemService;

    @Resource(name = "authCenterThreadPool")
    ExecutorService executorService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private KiopFeignClient kiopFeignClient;

    @Autowired
    private ResourceAuthClient adcClient;

    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private DeviceAblityClient deviceAblityClient;
    @Autowired
    private AvcsDeviceTypeDao avcsDeviceTypeDao;

    @Autowired
    private AdcService adcService;
    // 设备权限：单次授权数量
    @Value("${auth.center.device.maxSize: 1000}")
    private Integer size;
    // 设备权限：单次变更授权数量
    @Value("${auth.center.device.updateSize: 1000}")
    private Integer updateSize;

    // 设备权限：单次查询数量
    @Value("${auth.center.device.maxQuerySize: 1000}")
    private Integer maxQuerySize;

    @Value("${dispatch.common.kiop.enabled: true}")
    private Boolean kiopEnabled;

    @Value("${avcs.secretkey.callback.enableDelAblityCache: true}")
    private Boolean enableDelAblityCache;

    // 设备权限：设备查询地址
    @Value("${feign.client.config.default.connectTimeout: 2000}")
    private Integer connectTimeout;

    @Value("${feign.client.config.default.readTimeout: 20000}")
    private Integer readTimeout;

    @Value("${auth.center.sleep.enable: true}")
    private Boolean sleepEnable;

    @Value("${auth.center.sleep.time: 3000}")
    private Integer sleepTime;

    @Value("${auth.center.superRole: avcs3_role_super_admin}")
    private String superRole;

    // 授权超时时重试次数
    @Value("${auth.center.retry.count: 3}")
    private Integer retryCount;

    @Value("${auth.center.retry.preSleepTime: 3000}")
    private Integer preRetrySleep;

    @Override
    @AvcsServiceLockAno(group = RedisKey.DEVICE_AUTH_LOCAL)
    public void authByDevice(AuthDeviceDTO authDeviceDTO) {
        if (cacheService.hasKey(RedisKey.DEVICE_AUTH_LOCAL)) {
            // 有redis锁 device_auth_local 标识上个授权还在进行中
            sendError(AuthExceptionCode.AT205);
        } else {
            // 查询已授权的设备
            HeaderReq headerReq = RequestHeaderUtil.buildHeader();
            executorService.execute(() -> {
                if (CollectionUtils.isEmpty(authDeviceDTO.getDevices())) {
                    sendError(AuthExceptionCode.AT201);
                }
                if (StringUtils.isBlank(authDeviceDTO.getNewValidDate())) {
                    sendError(AuthExceptionCode.AT209);
                }
                try {
                    cacheService.set(RedisKey.DEVICE_AUTH_LOCAL, "");
                    deviceAuth(authDeviceDTO, headerReq);
                } catch (CommonException e) {
                    webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(e.getDispatchMessageList(), null));
                    webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
                    throw e;
                } catch (Exception e) {
                    log.error(AuthExceptionCode.AT206.getCode() + AuthExceptionCode.AT206.getMessage() + e, e);
                    sendError(AuthExceptionCode.AT206);
                } finally {
                    deleteCache();
                }
            });
        }
    }

    @Override
    @AvcsServiceLockAno(group = RedisKey.DEVICE_AUTH_LOCAL)
    public void updateAuthByDevice(AuthDeviceDTO authDeviceDTO) {
        if (cacheService.hasKey(RedisKey.DEVICE_AUTH_LOCAL)) {
            // 有redis锁 device_auth_local 标识上个授权还在进行中
            sendError(AuthExceptionCode.AT205);
        } else {
            executorService.execute(() -> {
                if (StringUtils.isBlank(authDeviceDTO.getNewValidDate()) || StringUtils.isBlank(authDeviceDTO.getOldValidDate())) {
                    sendError(AuthExceptionCode.AT209);
                }
                try {
                    cacheService.set(RedisKey.DEVICE_AUTH_LOCAL, "");
                    updateAuth(authDeviceDTO);
                } catch (CommonException e) {
                    webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(e.getDispatchMessageList(), null));
                    webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
                    throw e;
                } catch (Exception e) {
                    log.error(AuthExceptionCode.AT206.getCode() + AuthExceptionCode.AT206.getMessage() + e, e);
                    sendError(AuthExceptionCode.AT206);
                } finally {
                    deleteCache();
                }
            });
        }
    }

    @Override
    @AvcsServiceLockAno(group = RedisKey.DEVICE_AUTH_LOCAL)
    public void updateAuthByAllDevice(AuthDeviceDTO authDeviceDTO) {
        if (cacheService.hasKey(RedisKey.DEVICE_AUTH_LOCAL)) {
            // 有redis锁 device_auth_local 标识上个授权还在进行中
            sendError(AuthExceptionCode.AT205);
        } else {
            if (StringUtils.isBlank(authDeviceDTO.getNewValidDate())) {
                sendError(AuthExceptionCode.AT209);
            }
            if (StringUtils.isEmpty(authDeviceDTO.getOldValidDate())) {
                sendError(AuthExceptionCode.AT209);
            }
            if (CollectionUtils.isEmpty(authDeviceDTO.getRoleIds())) {
                sendError(AuthExceptionCode.AT103);
            }
            executorService.execute(() -> {
                try {
                    cacheService.set(RedisKey.DEVICE_AUTH_LOCAL, "");
                    AuthDeviceQueryBodyDTO bodyDTO = new AuthDeviceQueryBodyDTO();
                    BeanUtils.copyProperties(authDeviceDTO, bodyDTO);
                    bodyDTO.setPageNo(0);
                    bodyDTO.setPageSize(maxQuerySize);
                    bodyDTO.setDeviceGroupIds(authDeviceDTO.getGroupIds());
                    String validDate = authDeviceDTO.getOldValidDate().replaceAll("[-+,:+,\\s]", "");
                    bodyDTO.setValidDate(validDate);
                    bodyDTO.setDeviceQueryType(DeviceQueryTypeEnum.DATE.getType());
                    bodyDTO.setRoleIds(null);
                    bodyDTO.setWithRole(true);
                    HeaderReq headerReq = RequestHeaderUtil.buildHeader();
                    headerReq.setRoleIds(Strings.join(authDeviceDTO.getRoleIds(), ","));
                    String newvalidDate = authDeviceDTO.getNewValidDate().replaceAll("[-+,:+,\\s]", "");
                    List<SimpleAdcDeviceDTO> raDeviceDTOS = queryDevice(headerReq, bodyDTO,false,newvalidDate);
                    if (CollectionUtils.isNotEmpty(raDeviceDTOS)) {
                        List<DeviceDTO> devices = raDeviceDTOS.stream().map(device -> {
                            DeviceDTO deviceDTO = new DeviceDTO();
                            BeanUtils.copyProperties(device, deviceDTO);
                            List<ValidDateItemDTO> validDateItems = device.getValidDateItems();
                            if (CollectionUtils.isNotEmpty(validDateItems)) {
                                List<String> collect = validDateItems.stream().map(s -> s.getRoleId()).distinct().collect(Collectors.toList());
                                deviceDTO.setRoleIds(collect);
                            }
                            return deviceDTO;
                        }).collect(Collectors.toList());
                        authDeviceDTO.setDevices(devices);
                        updateAuth(authDeviceDTO);
                    } else {
                        sendAuthProcess("100", AuthTypeConstant.AUTH_UPDATE);
                        return;
                    }
                } catch (CommonException e) {
                    webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(e.getDispatchMessageList(), null));
                    webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
                    throw e;
                } catch (Exception e) {
                    log.error(AuthExceptionCode.AT206.getCode() + AuthExceptionCode.AT206.getMessage() + e, e);
                    sendError(AuthExceptionCode.AT206);
                } finally {
                    deleteCache();
                }
            });
        }
    }


    private void updateAuth(AuthDeviceDTO authDeviceDTO) {
        AuthDeviceDTO distinctAuthDevice = checkParam(authDeviceDTO);
        // 容量校验
        checkDevicesCapacity(distinctAuthDevice.getNewValidDate(), distinctAuthDevice.getDevices());
        // 授权新有效期
        String newValidDate = distinctAuthDevice.getNewValidDate().replaceAll("[-+,:+,\\s]", "");
        String oldValidDate = distinctAuthDevice.getOldValidDate().replaceAll("[-+,:+,\\s]", "");
        List<ValidDateUpdateItemVO> updateItemVOs = new ArrayList<>();
        for (int i = 0; i < authDeviceDTO.getDevices().size(); i++) {
            DeviceDTO device = authDeviceDTO.getDevices().get(i);
            String deviceId = device.getId();
            List<String> roleIds = device.getRoleIds();
            if (CollectionUtils.isEmpty(roleIds)) {
                log.error("变更授权:设备所属角色不存在，设备信息：" + device);
                continue;
            }
            for (String roleId : roleIds) {
                ValidDateUpdateItemVO validDateUpdateItemVO = new ValidDateUpdateItemVO();
                validDateUpdateItemVO.setRoleId(roleId);
                validDateUpdateItemVO.setDeviceId(deviceId);
                validDateUpdateItemVO.setNewValidDate(newValidDate);
                validDateUpdateItemVO.setOldValidDate(oldValidDate);
                updateItemVOs.add(validDateUpdateItemVO);
            }
        }
        sendAuthProcess(String.valueOf(0), AuthTypeConstant.AUTH_UPDATE);
        List<List<ValidDateUpdateItemVO>> partition = ListUtils.partition(updateItemVOs, updateSize);
        int authedNum = 0;
        for (int i = 0; i < partition.size(); i++) {
            List<ValidDateUpdateItemVO> list = partition.get(i);
            authedNum += list.size();
            // 根据有效期授权
            try {
                log.info("============ 变更授权总批次: " + partition.size() + "， 授权到第 " + (i + 1) + " 批次");
                commonAdcApiService.updateAuthorize(list);
            } catch (CommonException e) {
                // 超时重试机制 : 等待一秒后重试，重试次数达到最大时跳过
                log.info("============ 变更授权异常，异常信息 " + e.getMessage(), e);
                List<DispatchMessage> dispatchMessageList = e.getDispatchMessageList();
                boolean hasMessage = CollectionUtils.isNotEmpty(dispatchMessageList) && null != dispatchMessageList.get(0);
                int retry = retryCount;
                if (hasMessage && CommonCodeMessage.ECM402.getCode().equals(dispatchMessageList.get(0).getCode()) && retry > 0) {
                    for (int j = 0; j < retry; j++) {
                        try {
                            log.info("============ 变更授权异常后重试，重试次数  " + (j + 1));
                            TimeUnit.MILLISECONDS.sleep(preRetrySleep);
                            commonAdcApiService.updateAuthorize(list);
                            break;
                        } catch (InterruptedException interruptedException) {
                            log.info("============ 重试变更授权失败！异常信息:重试前等待异常" + e.getMessage(), e);
                        } catch (CommonException ex) {
                            hasMessage = CollectionUtils.isNotEmpty(dispatchMessageList) && null != dispatchMessageList.get(0);
                            if (hasMessage && CommonCodeMessage.ECM402.getCode().equals(dispatchMessageList.get(0).getCode())) {
                                log.info("============ 重试变更授权失败！异常信息 " + e.getMessage(), e);
                                continue;
                            }else if(hasMessage && CommonCodeMessage.ECM401.getCode().equals(dispatchMessageList.get(0).getCode())){
                                log.info("============ 设备权限连接失败！异常信息 " + e.getMessage(), e);
                                sendError(AuthExceptionCode.AT404);
                                return;
                            }
                        }
                    }
                }
            }
            float percent = (float) authedNum / (float) (updateItemVOs.size());
            sendAuthProcess(String.valueOf(percent * 100), AuthTypeConstant.AUTH_UPDATE);
        }
    }

    private void deviceAuthorizeByGroupId(AuthGroupDTO authGroupDTO) {
        String validDate = authGroupDTO.getNewValidDate().replaceAll("[-+,:+,\\s]", "");
        List<DeviceDTO> devices = authGroupDTO.getDevices();
        // 数量校验
        checkDevicesCapacityByGroup(authGroupDTO.getNewValidDate(), devices);
        List<DeviceGroupDTO> deviceGroupDTOS = devices.stream().map(dc -> {
            DeviceGroupDTO deviceGroupDTO = new DeviceGroupDTO();
            deviceGroupDTO.setDeviceId(dc.getId());
            return deviceGroupDTO;
        }).collect(Collectors.toList());
        // 开始授权
        sendAuthProcess(String.valueOf(0), AuthTypeConstant.AUTH_ADD);
        int authedNum = 0;
        List<List<DeviceGroupDTO>> partition = ListUtils.partition(deviceGroupDTOS, size);
        for (int j = 0; j < partition.size(); j++) {
            List<DeviceGroupDTO> list = partition.get(j);
            log.info("============ 授权总批次: " + partition.size() + "， 授权到第 " + (j + 1) + " 批次");
            authedNum += list.size();
            int retry = retryCount;
            try {
                commonAdcApiService.deviceAuthorizeByGroupId(authGroupDTO.getRoleIds(), list, validDate);
            } catch (CommonException e) {
                // 超时重试机制 : 等待一秒后重试，重试次数达到最大时跳过
                log.info("============ 授权异常，异常信息 " + e.getMessage(), e);
                List<DispatchMessage> dispatchMessageList = e.getDispatchMessageList();
                boolean hasMessage = CollectionUtils.isNotEmpty(dispatchMessageList) && null != dispatchMessageList.get(0);
                if (hasMessage && CommonCodeMessage.ECM402.getCode().equals(dispatchMessageList.get(0).getCode()) && retry > 0) {
                    for (int i = 0; i < retry; i++) {
                        try {
                            log.info("============ 异常后重试，重试次数  " + (i + 1));
                            TimeUnit.MILLISECONDS.sleep(preRetrySleep);
                            commonAdcApiService.deviceAuthorizeByGroupId(authGroupDTO.getRoleIds(), list, validDate);
                            break;
                        } catch (InterruptedException interruptedException) {
                            log.info("============ 重试授权失败！异常信息:重试前等待异常" + e.getMessage(), e);
                        } catch (CommonException ex) {
                            hasMessage = CollectionUtils.isNotEmpty(dispatchMessageList) && null != dispatchMessageList.get(0);
                            if (hasMessage && CommonCodeMessage.ECM402.getCode().equals(dispatchMessageList.get(0).getCode())) {
                                log.info("============ 重试授权失败！异常信息 " + e.getMessage(), e);
                                continue;
                            }else if(hasMessage && CommonCodeMessage.ECM401.getCode().equals(dispatchMessageList.get(0).getCode())){
                                // 设备权限服务超时熔断
                                log.info("============ 设备权限连接失败！异常信息 " + e.getMessage(), e);
                                sendError(AuthExceptionCode.AT404);
                                return;
                            }
                        }
                    }
                }
            }
            float percent = (float) authedNum / (float) deviceGroupDTOS.size();
            sendAuthProcess(String.valueOf(percent * 100), AuthTypeConstant.AUTH_ADD);
        }
        deleteCache();
        adcClient.refreshRoleValidDateCacheV2();
    }

    @Override
    public void deleteCache() {
        try {
            cacheService.delete(RedisKey.DEVICE_AUTH_LOCAL);
            cacheService.delete(RedisKey.AUTH_PROGRESS_BAR_PERCENT);
        } catch (Exception e) {
            log.error("清理授权缓存异常" + e.getMessage(), e);
        }
    }

    @Override
    @AvcsServiceLockAno(group = RedisKey.DEVICE_AUTH_LOCAL)
    public void cancelDeviceAuthByDate(String date) {
        // 1.获取所有的角色
        List<String> allRoleIds = getAllRoleIds();
        // 2.获取所有设备
        if (cacheService.hasKey(RedisKey.DEVICE_AUTH_LOCAL)) {
            throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT205));
        } else {
            if (CollectionUtils.isEmpty(allRoleIds)) {
                throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT307));
            }
            executorService.execute(() -> {
                DeviceIdsAndNumParamDTO deviceIdsAndNumParamDTO = new DeviceIdsAndNumParamDTO();
                deviceIdsAndNumParamDTO.setRoleIds(allRoleIds);
                deviceIdsAndNumParamDTO.setDeviceQueryType(DeviceQueryTypeEnum.DATE.getType());
                deviceIdsAndNumParamDTO.setValidDate(date);
                deviceIdsAndNumParamDTO.setSearchIds(true);
                // 根据所有角色id查出当前类型已授权的设备
                DevicesAndNumDTO devicesAndNumDTO = commonAdcApiService.queryDevicesAndNum(deviceIdsAndNumParamDTO);
                Set<SimpleAdcDeviceDTO> devicces = devicesAndNumDTO.getDevices();
                if (CollectionUtils.isEmpty(devicces)) {
                    return;
                }
                List<String> allDeviceIds = devicces.parallelStream().map(device -> device.getId()).collect(Collectors.toList());
                try {
                    cacheService.set(RedisKey.DEVICE_AUTH_LOCAL, "");
                    sendAuthProcess(String.valueOf(0), AuthTypeConstant.AUTH_CANCEL);
                    cancelAuth(allDeviceIds, allRoleIds, date);
                    // String cancleCode = secretKeyService.cancelActivation();
                    // webSocketService.sendWebSocket(null, AuthCenterConstant.authCancelCode, cancleCode);
                } catch (CommonException e) {
                    webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(e.getDispatchMessageList(), null));
                    webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
                    throw e;
                } catch (Exception e) {
                    log.error(AuthExceptionCode.AT206.getCode() + AuthExceptionCode.AT206.getMessage() + e, e);
                    sendError(AuthExceptionCode.AT206);
                } finally {
                    deleteCache();
                }
            });
        }
    }

    @Override
    @AvcsServiceLockAno(group = RedisKey.DEVICE_AUTH_LOCAL)
    public void cancelDeviceAuthByDateAnRoles(RoleDateDTO roleDateDTO) {
        // 1.获取所有的角色
        List<String> allRoleIds = roleDateDTO.getRoleIds();
        // 2.获取所有设备
        if (cacheService.hasKey(RedisKey.DEVICE_AUTH_LOCAL)) {
            throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT205));
        } else {
            if (CollectionUtils.isEmpty(allRoleIds)) {
                throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT307));
            }
            executorService.execute(() -> {
                DeviceIdsAndNumParamDTO deviceIdsAndNumParamDTO = new DeviceIdsAndNumParamDTO();
                deviceIdsAndNumParamDTO.setRoleIds(allRoleIds);
                deviceIdsAndNumParamDTO.setDeviceQueryType(DeviceQueryTypeEnum.DATE.getType());
                deviceIdsAndNumParamDTO.setValidDate(roleDateDTO.getDate());
                deviceIdsAndNumParamDTO.setSearchIds(true);
                // 根据所有角色id查出当前类型已授权的设备
                DevicesAndNumDTO devicesAndNumDTO = commonAdcApiService.queryDevicesAndNum(deviceIdsAndNumParamDTO);
                Set<SimpleAdcDeviceDTO> devicces = devicesAndNumDTO.getDevices();
                if (CollectionUtils.isEmpty(devicces)) {
                    return;
                }
                List<String> allDeviceIds = devicces.parallelStream().map(device -> device.getId()).collect(Collectors.toList());
                try {
                    cacheService.set(RedisKey.DEVICE_AUTH_LOCAL, "");
                    sendAuthProcess(String.valueOf(0), AuthTypeConstant.AUTH_CANCEL);
                    cancelAuth(allDeviceIds, allRoleIds, roleDateDTO.getDate());
                    // String cancleCode = secretKeyService.cancelActivation();
                    // webSocketService.sendWebSocket(null, AuthCenterConstant.authCancelCode, cancleCode);
                } catch (CommonException e) {
                    webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(e.getDispatchMessageList(), null));
                    webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
                    throw e;
                } catch (Exception e) {
                    log.error(AuthExceptionCode.AT206.getCode() + AuthExceptionCode.AT206.getMessage() + e, e);
                    sendError(AuthExceptionCode.AT206);
                } finally {
                    deleteCache();
                }
            });
        }
    }
//    @Override
//    public PagerResult<RaDeviceDTO> getCurrentUserAuthDevice(UnauthDevicesVO unauthDevicesVO) {
//        AuthDeviceQueryDTO authDeviceDTO = new AuthDeviceQueryDTO();
//        BeanUtils.copyProperties(unauthDevicesVO, authDeviceDTO);
//        HeaderReq req = RequestHeaderUtil.buildHeader();
//        String roleIds = req.getRoleIds();
//        List<String> roles = Arrays.asList(roleIds.split(","));
//        String url = "";
//        if (roles.contains(superRole)) {
//            // 超级管理员可查看全部设备
//            StringBuilder rootUrlBuilder = adcService.getRootUrlBuilder(authDeviceDTO);
//            url = rootUrlBuilder.toString();
//        } else {
//            // 非超管仅可见已授权设备
//            authDeviceDTO.setIsDevolve(true);
//            url = adcService.createUrl(authDeviceDTO);
//        }
//        RestTemplate restTemplate = getRestTemplate();
//        return adcService.queryDevices(url, restTemplate, req);
//    }

    private void authInvaliDevice(AuthDeviceDTO authDeviceDTO) {
        AuthDeviceDTO distinctAuthDevice = checkParam(authDeviceDTO);
        List<DeviceDTO> devices = distinctAuthDevice.getDevices();
        // 容量校验
        Map<String, List<String>> roleDeviceIds = new HashMap<>();
        for (DeviceDTO device : devices) {
            List<ValidDateItemDTO> validDateItems = device.getValidDateItems();
            if (CollectionUtils.isEmpty(validDateItems)) {
                continue;
            }
            for (ValidDateItemDTO validDateItem : validDateItems) {
                String roleId = validDateItem.getRoleId();
                List<String> dev = roleDeviceIds.get(roleId);
                if (null == dev) {
                    dev = new ArrayList<>();
                    roleDeviceIds.put(roleId, dev);
                }
                dev.add(device.getId());
            }
        }
        // 已授权设备数
        int total = 0;
        // 授权设备总数
        int authTolal = roleDeviceIds.entrySet().stream().map(s -> s.getValue().size()).reduce((v1, v2) -> v1 + v2).get();
        for (Map.Entry<String, List<String>> entry : roleDeviceIds.entrySet()) {
            String roleId = entry.getKey();
            List<String> deviceIds = entry.getValue();
            String valiDate = distinctAuthDevice.getNewValidDate();
            valiDate = valiDate.replaceAll("[-+,:+,\\s]", "");
            List<List<String>> partition = ListUtils.partition(deviceIds, size);
            for (List<String> list : partition) {
                total += list.size();
                // 根据有效期授权
                commonAdcApiService.deviceAuthorize(Collections.singletonList(roleId), list, valiDate);
                float percent = (float) total / ((float) authTolal * 2) + 0.5f;
                sendAuthProcess(String.valueOf(percent * 100), AuthTypeConstant.AUTH_UPDATE);
                sleep(percent);

            }
        }
        deleteCache();
        adcClient.refreshRoleValidDateCacheV2();
    }

    private void deviceAuth(AuthDeviceDTO authDeviceDTO, HeaderReq headerReq) {
        AuthDeviceDTO distinctAuthDevice = checkParam(authDeviceDTO);
        // 筛选出所选角色下不存在授权的设备
        List<DeviceDTO> unAuthDevices = extractedUnauthDevice(authDeviceDTO, headerReq);
        if (CollectionUtils.isEmpty(unAuthDevices)) {
            sendAuthProcess("100", AuthTypeConstant.AUTH_ADD);
            return;
        }
        checkDevicesCapacityByGroup(authDeviceDTO.getNewValidDate(), unAuthDevices);
        sendAuthProcess("0", AuthTypeConstant.AUTH_ADD);
        List<String> deviceIds = unAuthDevices.stream().map(s -> s.getId()).collect(Collectors.toList());
        List<List<String>> partition = ListUtils.partition(deviceIds, size);
        int total = 0;
        String valiDate = distinctAuthDevice.getNewValidDate();
        valiDate = valiDate.replaceAll("[-+,:+,\\s]", "");
        for (List<String> list : partition) {
            total += list.size();
            // 根据有效期授权
            commonAdcApiService.deviceAuthorize(authDeviceDTO.getRoleIds(), list, valiDate);
            float percent = (float) total / (float) (deviceIds.size());
            sendAuthProcess(String.valueOf(percent * 100), AuthTypeConstant.AUTH_ADD);
        }
        deleteCache();
        adcClient.refreshRoleValidDateCacheV2();
    }

    private void sendAuthProcess(String process, String type) {
        webSocketService.sendWebSocket(null, AuthCenterConstant.authProgressBar, JSON.toJSONString(new AuthProcessDTO(process, type)));
        cacheService.set(RedisKey.AUTH_PROGRESS_BAR_PERCENT, process);
    }

    private List<DeviceDTO> extractedUnauthDevice(AuthDeviceDTO authDeviceDTO, HeaderReq headerReq) {
        AuthDeviceQueryBodyDTO bodyDTO = new AuthDeviceQueryBodyDTO();
        bodyDTO.setPageNo(0);
        bodyDTO.setPageSize(maxQuerySize);
        bodyDTO.setValidDate(authDeviceDTO.getNewValidDate());
        bodyDTO.setWithRole(true);
        bodyDTO.setDeviceIds(authDeviceDTO.getDevices()
                .stream()
                .map(device -> device.getId())
                .collect(Collectors.toList()));
        bodyDTO.setDeviceGroupIds(authDeviceDTO.getGroupIds());
        bodyDTO.setDeviceQueryType(DeviceQueryTypeEnum.DATE.getType());
        List<String> roleIds = authDeviceDTO.getRoleIds();
        String roleStr = Strings.join(roleIds, ",");
        headerReq.setRoleIds(roleStr);
        String newValidDate =authDeviceDTO.getNewValidDate().replaceAll("[-+,:+,\\s]", "");
        List<SimpleAdcDeviceDTO> raDeviceDTOS = queryDevice(headerReq, bodyDTO,false,authDeviceDTO.getNewValidDate());
        List<DeviceDTO> unauthed = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(raDeviceDTOS)) {
            Iterator<SimpleAdcDeviceDTO> iterator = raDeviceDTOS.iterator();
            while (iterator.hasNext()) {
                SimpleAdcDeviceDTO next = iterator.next();
                if(CollectionUtils.isNotEmpty(next.getValidDateItems())){
                    List<String> collect = next.getValidDateItems().stream().filter(s -> newValidDate.equals(s.getValidDate()))
                            .map(vo -> vo.getRoleId()).collect(Collectors.toList());
                    if (!collect.containsAll(roleIds)) {
                        DeviceDTO deviceDTO = new DeviceDTO();
                        BeanUtils.copyProperties(next,deviceDTO);
                        unauthed.add(deviceDTO);
                    }
                }
            }
            List<String> collect = raDeviceDTOS.stream().map(s -> s.getId()).collect(Collectors.toList());
            // 添加完全未授权设备
            unauthed.addAll(authDeviceDTO.getDevices()
                    .stream()
                    .filter(device -> !collect.contains(device.getId()))
                    .collect(Collectors.toList()));
            return unauthed;
        } else {
            return authDeviceDTO.getDevices();
        }
    }

    // 校验各类型设备有没有超出可用容量
    private void checkDevicesCapacity(String validDate, List<DeviceDTO> devices) {
        //有效期详情容量缓存
        if (Objects.isNull(cacheLicenseMap)) {
            cacheLicenseMap = secretKeyService.getCapacityPackage();
        }
        List<DispatchMessage> dispatchMessageList = new ArrayList<>();
        DetailDTO codeInfo = cacheLicenseMap.get(validDate);
        if (null == codeInfo) {
            sendError(AuthExceptionCode.AT303);
        }
        List<String> allRoleIds = getAllRoleIds();
        //临时设备数量
        Map<String, Set<String>> aMemSetMap = aMemService.searchAllDevices();
        String ipcDeviceType = "";
        List<String> ipcDeviceIds = new ArrayList<>();
        String terminalDeviceType = "";
        List<String> terminalDeviceIds = new ArrayList<>();
        String telephoneDeviceType = "";
        List<String> telephoneDeviceIds = new ArrayList<>();
        String pttDeviceType = "";
        List<String> pttDeviceIds = new ArrayList<>();
        String appType = "";
        List<String> appIds = new ArrayList<>();
        List<AvcsDeviceType> avcsDeviceTypes = avcsDeviceTypeDao.findAll();
        Map<String, List<String>> types = avcsDeviceTypes.stream().collect(Collectors.groupingBy(AvcsDeviceType::getCategory
                , Collectors.mapping(AvcsDeviceType::getDeviceType, Collectors.toList())));
        for (DeviceDTO deviceDTO : devices) {
            if (StringUtils.isEmpty(deviceDTO.getDeviceType())) {
                sendError(AuthExceptionCode.AT207);
            }
            if (types.get(DeviceCategoryEnum.IPC.getCategory()).contains(deviceDTO.getDeviceType())) {
                ipcDeviceType = ipcDeviceType + deviceDTO.getDeviceType() + ",";
                ipcDeviceIds.add(deviceDTO.getId());
            }
            if (types.get(DeviceCategoryEnum.TERMINAL.getCategory()).contains(deviceDTO.getDeviceType())) {
                terminalDeviceType = terminalDeviceType + deviceDTO.getDeviceType() + ",";
                terminalDeviceIds.add(deviceDTO.getId());
            }
            if (types.get(DeviceCategoryEnum.TELEPHONE.getCategory()).contains(deviceDTO.getDeviceType())) {
                telephoneDeviceType = telephoneDeviceType + deviceDTO.getDeviceType() + ",";
                telephoneDeviceIds.add(deviceDTO.getId());
            }
            if (types.get(DeviceCategoryEnum.PTT.getCategory()).contains(deviceDTO.getDeviceType())) {
                pttDeviceType = pttDeviceType + deviceDTO.getDeviceType() + ",";
                pttDeviceIds.add(deviceDTO.getId());
            }
            if (types.get(DeviceCategoryEnum.APP.getCategory()).contains(deviceDTO.getDeviceType())) {
                appType = appType + deviceDTO.getDeviceType() + ",";
                appIds.add(deviceDTO.getId());
            }
        }
        List<Boolean> hasErrorList = new ArrayList<>();
        List<String> ipcs = types.get(DeviceCategoryEnum.IPC.getCategory());
        boolean hasError = checkCapiticy(ipcDeviceIds, codeInfo, allRoleIds, aMemSetMap, dispatchMessageList,
                ipcDeviceType, DeviceCategoryEnum.IPC, ipcs);
        hasErrorList.add(hasError);
        List<String> ters = types.get(DeviceCategoryEnum.TERMINAL.getCategory());
        hasError = checkCapiticy(terminalDeviceIds, codeInfo, allRoleIds, aMemSetMap, dispatchMessageList,
                terminalDeviceType, DeviceCategoryEnum.TERMINAL, ters);
        hasErrorList.add(hasError);
        List<String> telephones = types.get(DeviceCategoryEnum.TELEPHONE.getCategory());
        hasError = checkCapiticy(telephoneDeviceIds, codeInfo, allRoleIds, aMemSetMap, dispatchMessageList,
                telephoneDeviceType, DeviceCategoryEnum.TELEPHONE, telephones);
        hasErrorList.add(hasError);
        List<String> ptts = types.get(DeviceCategoryEnum.PTT.getCategory());
        hasError = checkCapiticy(pttDeviceIds, codeInfo, allRoleIds, aMemSetMap, dispatchMessageList,
                pttDeviceType, DeviceCategoryEnum.PTT, ptts);
        hasErrorList.add(hasError);
        List<String> apps = types.get(DeviceCategoryEnum.APP.getCategory());
        hasError = checkCapiticy(appIds, codeInfo, allRoleIds, aMemSetMap, dispatchMessageList,
                appType, DeviceCategoryEnum.APP, apps);
        hasErrorList.add(hasError);
        if (hasErrorList.contains(true)) {
            webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(dispatchMessageList, null));
            webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
            throw CommonException.error(dispatchMessageList);
        }
    }


    // 分组授权-校验各类型设备有没有超出可用容量
    private void checkDevicesCapacityByGroup(String validDate, List<DeviceDTO> devices) {
        //有效期详情容量缓存
        if (Objects.isNull(cacheLicenseMap)) {
            cacheLicenseMap = secretKeyService.getCapacityPackage();
        }
        List<DispatchMessage> dispatchMessageList = new ArrayList<>();
        DetailDTO codeInfo = cacheLicenseMap.get(validDate);
        if (null == codeInfo) {
            sendError(AuthExceptionCode.AT303);
        }
        List<AvcsDeviceType> avcsDeviceTypes = avcsDeviceTypeDao.findAll();
        Map<String, List<String>> types = avcsDeviceTypes.stream().collect(Collectors.groupingBy(AvcsDeviceType::getCategory
                , Collectors.mapping(AvcsDeviceType::getDeviceType, Collectors.toList())));
        List<UsedDeviceAmountDTO> usedDeviceAmount = getUsedDeviceAmountByExpTime(validDate);
        int usedIpcUseDeviceCount = 0;
        int usedTerminalDeviceCount = 0;
        int usedTelephoneDeviceCount = 0;
        int usedPttDeviceCount = 0;
        int usedAppCount = 0;
        for (UsedDeviceAmountDTO usedDeviceAmountDTO : usedDeviceAmount) {
            if (types.get(DeviceCategoryEnum.IPC.getCategory()).contains(usedDeviceAmountDTO.getDeviceType())) {
                usedIpcUseDeviceCount = usedDeviceAmountDTO.getInUsedAmount();
            }
            if (types.get(DeviceCategoryEnum.TERMINAL.getCategory()).contains(usedDeviceAmountDTO.getDeviceType())) {
                usedTerminalDeviceCount = usedDeviceAmountDTO.getInUsedAmount();
            }
            if (types.get(DeviceCategoryEnum.TELEPHONE.getCategory()).contains(usedDeviceAmountDTO.getDeviceType())) {
                usedTelephoneDeviceCount = usedDeviceAmountDTO.getInUsedAmount();
            }
            if (types.get(DeviceCategoryEnum.PTT.getCategory()).contains(usedDeviceAmountDTO.getDeviceType())) {
                usedPttDeviceCount = usedDeviceAmountDTO.getInUsedAmount();
            }
            if (types.get(DeviceCategoryEnum.APP.getCategory()).contains(usedDeviceAmountDTO.getDeviceType())) {
                usedAppCount = usedDeviceAmountDTO.getInUsedAmount();
            }
        }

        String ipcDeviceType = "";
        int ipcDeviceCount = 0;
        String terminalDeviceType = "";
        int terminalDeviceCount = 0;
        String telephoneDeviceType = "";
        int telephoneDeviceCount = 0;
        String pttDeviceType = "";
        int pttDeviceCount = 0;
        String appType = "";
        int appCount = 0;

        for (DeviceDTO deviceDTO : devices) {
            if (StringUtils.isEmpty(deviceDTO.getDeviceType())) {
                sendError(AuthExceptionCode.AT207);
            }
            if (types.get(DeviceCategoryEnum.IPC.getCategory()).contains(deviceDTO.getDeviceType())) {
                ipcDeviceType = ipcDeviceType + deviceDTO.getDeviceType() + ",";
                if (null == deviceDTO.getHasValidDate() || !deviceDTO.getHasValidDate()) {
                    ipcDeviceCount = ipcDeviceCount + 1;
                }
            }
            if (types.get(DeviceCategoryEnum.TERMINAL.getCategory()).contains(deviceDTO.getDeviceType())) {
                terminalDeviceType = terminalDeviceType + deviceDTO.getDeviceType() + ",";

                if (null == deviceDTO.getHasValidDate() || !deviceDTO.getHasValidDate()) {
                    terminalDeviceCount = terminalDeviceCount + 1;
                }
            }
            if (types.get(DeviceCategoryEnum.TELEPHONE.getCategory()).contains(deviceDTO.getDeviceType())) {
                telephoneDeviceType = telephoneDeviceType + deviceDTO.getDeviceType() + ",";
                if (null == deviceDTO.getHasValidDate() || !deviceDTO.getHasValidDate()) {
                    telephoneDeviceCount = telephoneDeviceCount + 1;
                }
            }
            if (types.get(DeviceCategoryEnum.PTT.getCategory()).contains(deviceDTO.getDeviceType())) {
                pttDeviceType = pttDeviceType + deviceDTO.getDeviceType() + ",";
                if (null == deviceDTO.getHasValidDate() || !deviceDTO.getHasValidDate()) {
                    pttDeviceCount = pttDeviceCount + 1;
                }
            }
            if (types.get(DeviceCategoryEnum.APP.getCategory()).contains(deviceDTO.getDeviceType())) {
                appType = appType + deviceDTO.getDeviceType() + ",";
                if (null == deviceDTO.getHasValidDate() || !deviceDTO.getHasValidDate()) {
                    appCount = appCount + 1;
                }
            }
        }
        List<Boolean> hasErrorList = new ArrayList<>();
        boolean hasError = checkCapiticyByGroup(usedIpcUseDeviceCount, ipcDeviceCount, codeInfo, dispatchMessageList, ipcDeviceType, DeviceCategoryEnum.IPC);
        hasErrorList.add(hasError);
        hasError = checkCapiticyByGroup(usedTerminalDeviceCount, terminalDeviceCount, codeInfo, dispatchMessageList, terminalDeviceType, DeviceCategoryEnum.TERMINAL);
        hasErrorList.add(hasError);
        hasError = checkCapiticyByGroup(usedTelephoneDeviceCount, telephoneDeviceCount, codeInfo, dispatchMessageList, telephoneDeviceType, DeviceCategoryEnum.TELEPHONE);
        hasErrorList.add(hasError);
        hasError = checkCapiticyByGroup(usedPttDeviceCount, pttDeviceCount, codeInfo, dispatchMessageList, pttDeviceType, DeviceCategoryEnum.PTT);
        hasErrorList.add(hasError);
        hasError = checkCapiticyByGroup(usedAppCount, appCount, codeInfo, dispatchMessageList, appType, DeviceCategoryEnum.APP);
        hasErrorList.add(hasError);
        if (hasErrorList.contains(true)) {
            webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(dispatchMessageList, null));
            webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
            throw CommonException.error(dispatchMessageList);
        }
    }

    @Override
    @AvcsServiceLockAno(group = RedisKey.DEVICE_AUTH_LOCAL)
    public void authByGroup(AuthGroupDTO authGroupDTO) {
        if (cacheService.hasKey(RedisKey.DEVICE_AUTH_LOCAL)) {
            sendError(AuthExceptionCode.AT205);
        } else {
            HeaderReq headerReq = RequestHeaderUtil.buildHeader();
            executorService.execute(() -> {
                if (CollectionUtils.isEmpty(authGroupDTO.getDeviceTypes())) {
                    sendError(AuthExceptionCode.AT203);
                }
                if (CollectionUtils.isEmpty(authGroupDTO.getGroupIds())) {
                    sendError(AuthExceptionCode.AT204);
                }
                if (CollectionUtils.isEmpty(authGroupDTO.getRoleIds())) {
                    sendError(AuthExceptionCode.AT200);
                }
                if (StringUtils.isEmpty(authGroupDTO.getNewValidDate())) {
                    sendError(AuthExceptionCode.AT209);
                }
                try {
                    cacheService.set(RedisKey.DEVICE_AUTH_LOCAL, "");
                    List<SimpleAdcDeviceDTO> unauthDevices = getUnauthDevices(authGroupDTO, headerReq);
                    List<DeviceDTO> devices = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(unauthDevices)) {
                        devices.addAll(unauthDevices.parallelStream().map(res -> {
                            DeviceDTO deviceDTO = new DeviceDTO();
                            BeanUtils.copyProperties(res, deviceDTO);
                            if (Boolean.TRUE.equals(authGroupDTO.getIsDevolve())){
                                // 权限下放开启时，数量校验仅在超管授权时
                                // 权限下放开启时，查询当前登录人角色下的已授权设备，授权给选中的角色，故取消数量校验
                                deviceDTO.setHasValidDate(true);
                            }
                            return deviceDTO;
                        }).collect(Collectors.toList()));
                    } else {
                        sendAuthProcess(String.valueOf(100), AuthTypeConstant.AUTH_ADD);
                        return;
                    }
                    authGroupDTO.setDevices(devices);
                    deviceAuthorizeByGroupId(authGroupDTO);
                } catch (CommonException e) {
                    webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(e.getDispatchMessageList(), null));
                    webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
                    throw e;
                } catch (Exception e) {
                    List<DispatchMessage> dispatchMessageList = new ArrayList<>();
                    dispatchMessageList.add(DispatchMessage.message(AuthExceptionCode.AT206));
                    webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(dispatchMessageList, null));
                    webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
                    log.error(ExceptionUtils.getFullStackTrace(e));
                    throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT206));
                } finally {
                    deleteCache();
                }
            });
        }
    }

    // 查询未授权设备
    private List<SimpleAdcDeviceDTO> getUnauthDevices(AuthGroupDTO authGroupDTO, HeaderReq headerReq) {
        AuthDeviceQueryBodyDTO bodyDTO = new AuthDeviceQueryBodyDTO();
        BeanUtils.copyProperties(authGroupDTO, bodyDTO);
        bodyDTO.setPageNo(0);
        bodyDTO.setPageSize(maxQuerySize);
        bodyDTO.setDeviceGroupIds(authGroupDTO.getGroupIds());
        String validDate = authGroupDTO.getNewValidDate().replaceAll("[-+,:+,\\s]", "");
        List<String> roleIds = authGroupDTO.getRoleIds();
        // 查询未授权设备
        if (!authGroupDTO.getIsDevolve()) {
            bodyDTO.setExcludeRole(true);
            bodyDTO.setMultiExcludeRoleModel(AuthDeviceQueryBodyConstant.MULTI_EXCLUDE_ROLE_MODEL_OR);
            List<String> roleDates = new ArrayList<>();
            bodyDTO.setRoleIds(roleDates);
            for (int i = 0; i < roleIds.size(); i++) {
                String roleDate = roleIds.get(i) + "_" + validDate;
                roleDates.add(roleDate);
            }
        } else {
            // 权限下放条件下查询给定角色有效期未授权的设备
            bodyDTO.setValidDate(authGroupDTO.getNewValidDate());
            bodyDTO.setDeviceQueryType(DeviceQueryTypeEnum.DATE.getType());
            bodyDTO.setRoleIds(null);
        }
        return queryDevice(headerReq, bodyDTO,true,validDate);
    }
    /**
     * @Param headerReq 请求头
     * @Param bodyDTO 请求参数
     * @Param isFilterRoles 返回结果集是否过滤掉 roleIds、validDateItems 字段
     * @Param newValidDate 用于判断返回设备是否有此有效期的授权
     * @Return List<SimpleAdcDeviceDTO> 设备信息，如果入参newValidDate=true则 SimpleAdcDeviceDTO中roleIds=null和validDateItems=null
     */
    private List<SimpleAdcDeviceDTO> queryDevice(HeaderReq headerReq, AuthDeviceQueryBodyDTO bodyDTO,Boolean isFilterRoles, String newValidDate) {
        RestTemplate restTemplate = getRestTemplate();
        String url = adcService.createUrl();
        PagerResult<SimpleAdcDeviceDTO> pagerResult = adcService.queryDevices(url, restTemplate, headerReq, bodyDTO, isFilterRoles,  newValidDate);
        if (null == pagerResult || CollectionUtils.isEmpty(pagerResult.getData())) {
            return null;
        }
        // 异步查询
        if (pagerResult.getTotalPages() > 1) {
            bodyDTO.setPageNo(1);
            List<SimpleAdcDeviceDTO> list = multiQueryDevices(restTemplate, pagerResult.getTotalPages(), headerReq, bodyDTO, isFilterRoles,  newValidDate);
            pagerResult.getData().addAll(list);
        }
        return pagerResult.getData();
    }

    private List<SimpleAdcDeviceDTO> multiQueryDevices(RestTemplate restTemplate,
                                                Long totalPage, HeaderReq headerReq, AuthDeviceQueryBodyDTO bodyDTO,Boolean isFilterRoles,String newValidDate) {
        ConcurrentLinkedDeque<Future<List<SimpleAdcDeviceDTO>>> concurrentLinkedDeque = new ConcurrentLinkedDeque<>();
        List<SimpleAdcDeviceDTO> list = new ArrayList<>();
        for (int i = bodyDTO.getPageNo(); i < totalPage; i++) {
            AuthDeviceQueryBodyDTO reqBody = new AuthDeviceQueryBodyDTO();
            BeanUtils.copyProperties(bodyDTO, reqBody);
            reqBody.setPageNo(i);
            DeviceQueryThread deviceCountThread = new DeviceQueryThread(adcService, restTemplate, headerReq, reqBody, isFilterRoles, newValidDate);
            Future<List<SimpleAdcDeviceDTO>> submit = executorService.submit(deviceCountThread);
            concurrentLinkedDeque.addFirst(submit);
        }
        boolean hasError = false;
        while (concurrentLinkedDeque.size() > 0) {
            Future<List<SimpleAdcDeviceDTO>> first = concurrentLinkedDeque.getFirst();
            boolean done = first.isDone();
            if (done) {
                log.info("查询子线程:" + first + "完成情况--->" + done);
                try {
                    List<SimpleAdcDeviceDTO> devices = first.get();
                    if (CollectionUtils.isNotEmpty(devices)) {
                        list.addAll(devices);
                    }
                } catch (Exception e) {
                    log.error(AuthExceptionCode.AT402.getMessage() + e.getMessage(), e);
                    hasError = true;
                    continue;
                }finally {
                    concurrentLinkedDeque.removeFirst();
                }
            }
        }
        if(concurrentLinkedDeque.size() > 0){
            concurrentLinkedDeque.clear();
        }
        if(hasError){
            webSocketService.sendWebSocket(null, "/message",
                    DispatchResponseMessage.error(DispatchMessage.message(AuthExceptionCode.AT315)));
        }
        return list;
    }

    private RestTemplate getRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }


//    private void deviceGroupDTOSetGroupId(AuthGroupDTO
//                                                  authGroupDTO, List<DeviceGroupDTO> deviceGroupList, Set<SimpleAdcDeviceDTO> deviceDTOS) {
//        List<String> deviceGroupIds = authGroupDTO.getGroupIds();
//        //根据选择的分组与查询结果分组进行交集取值
//        List<DeviceDTO> authDevices = new ArrayList<>();
//        for (SimpleAdcDeviceDTO deviceDTO : deviceDTOS) {
//            DeviceGroupDTO deviceGroupDTO = new DeviceGroupDTO();
//            deviceGroupDTO.setDeviceId(deviceDTO.getId());
//            DeviceDTO devDto = new DeviceDTO();
//            devDto.setId(deviceDTO.getId());
//            devDto.setDeviceName(deviceDTO.getDeviceName());
//            devDto.setGroupId(deviceDTO.getGroupId());
//            devDto.setDeviceType(deviceDTO.getDeviceType());
//            devDto.setHasValidDate(deviceDTO.getHasValidDate());
//            devDto.setRoleIds(deviceDTO.getRoleIds());
//            authDevices.add(devDto);
//            String tmpGroupId = "";
//            List<String> raGroupIds = new ArrayList<>(Arrays.asList(deviceDTO.getGroupId().split("\\|")));
//            if (!deviceGroupIds.isEmpty() && !raGroupIds.isEmpty()) {
//                Set<String> result = deviceGroupIds.stream()
//                        .distinct()
//                        .filter(raGroupIds::contains)
//                        .collect(Collectors.toSet());
//                tmpGroupId = String.join("\\|", result);
//            }
//            deviceGroupDTO.setGroupId(tmpGroupId);
//            deviceGroupList.add(deviceGroupDTO);
//        }
//        authGroupDTO.setDevices(authDevices);
//    }

    @Override
    public List<UsedDeviceAmountDTO> getUsedDeviceAmountByExpTime(String validDate) {
        List<String> allRoleIds = getAllRoleIds();
        List<UsedDeviceAmountDTO> list = new ArrayList<>();
        Map<String, DetailDTO> codeInfo = secretKeyService.getCapacityPackage();
        DetailDTO detailDTO = codeInfo.get(validDate);
        List<AvcsDeviceType> cates = avcsDeviceTypeDao.findAll();
        Map<String, List<AvcsDeviceType>> collect = cates.stream().collect(Collectors.groupingBy(AvcsDeviceType::getCategory));
        // 存储线程运行完数据
        ConcurrentLinkedDeque<Future<UsedDeviceAmountDTO>> concurrentLinkedDeque = new ConcurrentLinkedDeque<>();
        for (DeviceCategoryEnum value : DeviceCategoryEnum.values()) {
            if (value == DeviceCategoryEnum.OTHER) {
                break;
            }
//            setCountNum(validDate, allRoleIds, list, codeInfo, detailDTO, collect, value);
            DeviceCountThread deviceCountThread = new DeviceCountThread(validDate, allRoleIds, codeInfo, detailDTO, collect, value, this);
            Future<UsedDeviceAmountDTO> submit = executorService.submit(deviceCountThread);
            concurrentLinkedDeque.addFirst(submit);
        }
        while (concurrentLinkedDeque.size() > 0) {
            Future<UsedDeviceAmountDTO> first = concurrentLinkedDeque.getFirst();
            if (first.isDone()) {
                try {
                    UsedDeviceAmountDTO usedDeviceAmountDTO = null;
                    usedDeviceAmountDTO = first.get();
                    list.add(usedDeviceAmountDTO);

                } catch (Exception e) {
                    log.error(AuthExceptionCode.AT402.getMessage() + e.getMessage(), e);
                    throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT402));
                } finally {
                    concurrentLinkedDeque.removeFirst();
                }
            }
        }
        return list;
    }
    // 获取融合平台(true)和轻服务(false)所有角色

    /**
     * 获取融合平台或轻服务角色
     *
     * @param isPlat:融合平台(true) 轻服务(false)
     */
    private List<String> getPlatformRoles(boolean isPlat) {
        RoleQueryDTO roleQueryDTO = new RoleQueryDTO();
        roleQueryDTO.setStatus(RbacStatusEnum.VALID.value());
        List<ProjectRoleDTO> projectRoleDTOS = commonRbacApiService.queryProjectRole(roleQueryDTO);
        List<String> allRoleIds = new ArrayList<>();
        for (ProjectRoleDTO projectRoleDTO : projectRoleDTOS) {
            if (isPlat && "ty-dispatch-server".equals(projectRoleDTO.getClientId())) {
                projectRoleDTO.getRoles().forEach(authRole -> {
                    //拿出所有角色id
                    allRoleIds.add(authRole.getId());
                });
                break;
            } else if (!isPlat && "dispatch-micoservice".equals(projectRoleDTO.getClientId())) {
                projectRoleDTO.getRoles().forEach(authRole -> {
                    //拿出所有角色id
                    allRoleIds.add(authRole.getId());
                });
                break;
            }
        }

        return allRoleIds;
    }

    // 获取融合平台和轻服务的所有角色
    @Override
    public List<String> getAllRoleIds() {
        RoleQueryDTO roleQueryDTO = new RoleQueryDTO();
        roleQueryDTO.setStatus(RbacStatusEnum.VALID.value());
        List<ProjectRoleDTO> projectRoleDTOS = commonRbacApiService.queryProjectRole(roleQueryDTO);
        List<String> allRoleIds = new ArrayList<>();
        for (ProjectRoleDTO projectRoleDTO : projectRoleDTOS) {
            projectRoleDTO.getRoles().forEach(authRole -> {
                //拿出所有角色id
                allRoleIds.add(authRole.getId());
            });
        }
        // 开放平台
        List<ApiKeyInfoDTO> apiKeyInfs = getAllApiKey();
        if (CollectionUtils.isNotEmpty(apiKeyInfs)) {
            allRoleIds.addAll(apiKeyInfs.stream().map(apiKeyInf -> apiKeyInf.getApiKey()).collect(Collectors.toList()));
        }
        return allRoleIds;
    }

    @Override
    @AvcsServiceLockAno(group = RedisKey.DEVICE_AUTH_LOCAL)
    public void cancelDeviceAuth(AuthDeviceDTO authDeviceDTO) {
        if (cacheService.hasKey(RedisKey.DEVICE_AUTH_LOCAL)) {
            sendError(AuthExceptionCode.AT205);
        } else {
            executorService.execute(() -> {
                try {
                    cacheService.set(RedisKey.DEVICE_AUTH_LOCAL, "");
                    if (CollectionUtils.isEmpty(authDeviceDTO.getDevices())) {
                        if (CollectionUtils.isEmpty(authDeviceDTO.getGroupIds())) {
                            sendError(AuthExceptionCode.AT201);
                        }
                    }
                    if (CollectionUtils.isEmpty(authDeviceDTO.getRoleIds())) {
                        sendError(AuthExceptionCode.AT200);
                    }
                    if (StringUtils.isEmpty(authDeviceDTO.getOldValidDate())) {
                        sendError(AuthExceptionCode.AT209);
                    }
                    //取消授权时，如果有分组id,取分组下所有设备类型的有效期及角色下设备ids
                    if (CollectionUtils.isNotEmpty(authDeviceDTO.getGroupIds())) {
                        DeviceIdsAndNumParamDTO deviceIdsAndNumParamDTO = new DeviceIdsAndNumParamDTO();
                        deviceIdsAndNumParamDTO.setDeviceTypes(authDeviceDTO.getDeviceTypes());
                        deviceIdsAndNumParamDTO.setDeviceGroupIds(authDeviceDTO.getGroupIds());
                        deviceIdsAndNumParamDTO.setSearchIds(true);
                        deviceIdsAndNumParamDTO.setIsQueryDeviceGroup(true);
                        deviceIdsAndNumParamDTO.setValidDate(authDeviceDTO.getOldValidDate().replaceAll("[-+,:+,\\s]", ""));
                        deviceIdsAndNumParamDTO.setRoleIds(authDeviceDTO.getRoleIds());
                        deviceIdsAndNumParamDTO.setDeviceQueryType(DeviceQueryTypeEnum.ALL.getType());
                        // 根据分组id查询分组下所有的设备
                        DevicesAndNumDTO deviceIdsAndNumDTO = commonAdcApiService.queryDevicesAndNum(deviceIdsAndNumParamDTO);
                        if (CollectionUtils.isNotEmpty(deviceIdsAndNumDTO.getDevices())) {
                            List<DeviceDTO> devices = deviceIdsAndNumDTO.getDevices().stream().map(device -> {
                                DeviceDTO deviceDTO = new DeviceDTO();
                                deviceDTO.setId(device.getId());
                                deviceDTO.setDeviceName(device.getDeviceName());
                                deviceDTO.setGroupId(device.getGroupId());
                                deviceDTO.setDeviceType(device.getDeviceType());
                                deviceDTO.setHasValidDate(device.getHasValidDate());
                                deviceDTO.setRoleIds(device.getRoleIds());
                                return deviceDTO;
                            }).collect(Collectors.toList());
                            authDeviceDTO.getDevices().addAll(devices);
                        }
                    }
                    AuthDeviceDTO distinctAuthDevice = checkParam(authDeviceDTO);
                    List<String> ids = distinctAuthDevice.getDevices().stream().map(device -> device.getId()).collect(Collectors.toList());
                    sendAuthProcess(String.valueOf(0), AuthTypeConstant.AUTH_CANCEL);
                    cancelAuth(ids, distinctAuthDevice.getRoleIds(), authDeviceDTO.getOldValidDate());
                    deleteCache();
                    clearAvcsCache(ids);
                } catch (CommonException e) {
                    webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(e.getDispatchMessageList(), null));
                    webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
                    throw e;
                } catch (Exception e) {
                    log.error(AuthExceptionCode.AT206.getCode() + AuthExceptionCode.AT206.getMessage() + e, e);
                    sendError(AuthExceptionCode.AT206);
                } finally {
                    deleteCache();
                }
            });
        }
    }

    private void clearAvcsCache(List<String> ids) {
        // 调用融合通信删除设备能力授权缓存
        if (enableDelAblityCache) {
            try {
                deviceAblityClient.deleteKeys(ids);
            } catch (Exception e) {
                log.error(AuthExceptionCode.AT206.getCode() + AuthExceptionCode.AT206.getMessage() + e, e);
            }
        }
    }

    @Override
    @AvcsServiceLockAno(group = RedisKey.DEVICE_AUTH_LOCAL)
    public void cancelDeviceAuthByType(CancelAuthDTO cancelAuthDTO) {
        if (cacheService.hasKey(RedisKey.DEVICE_AUTH_LOCAL)) {
            sendError(AuthExceptionCode.AT205);
        } else {
            if (CollectionUtils.isEmpty(cancelAuthDTO.getRoleIds())) {
                sendError(AuthExceptionCode.AT200);
            }
            if (StringUtils.isEmpty(cancelAuthDTO.getVlidDate())) {
                sendError(AuthExceptionCode.AT209);
            }
            String validDate = cancelAuthDTO.getVlidDate().replaceAll("[-+,:+,\\s]", "");
            executorService.execute(() -> {
                try {
                    cacheService.set(RedisKey.DEVICE_AUTH_LOCAL, "");
                    List<String> collect = cancelAuthDTO.getRoleIds().stream().distinct().collect(Collectors.toList());
                    DeviceIdsAndNumParamDTO deviceIdsAndNumParamDTO = new DeviceIdsAndNumParamDTO();
                    deviceIdsAndNumParamDTO.setRoleIds(collect);
                    deviceIdsAndNumParamDTO.setDeviceTypes(cancelAuthDTO.getDeviceTypes());
                    deviceIdsAndNumParamDTO.setSearchIds(true);
                    deviceIdsAndNumParamDTO.setValidDate(validDate);
                    deviceIdsAndNumParamDTO.setDeviceQueryType(DeviceQueryTypeEnum.DATE.getType());
                    // 根据所有角色id查出当前类型已授权的设备
                    DevicesAndNumDTO devicesAndNumDTO = commonAdcApiService.queryDevicesAndNum(deviceIdsAndNumParamDTO);
                    Set<SimpleAdcDeviceDTO> devicces = devicesAndNumDTO.getDevices();
                    if (CollectionUtils.isEmpty(devicces)) {
                        sendError(AuthExceptionCode.AT306);
                    }
                    List<String> allDeviceIds = devicces.stream().map(device -> device.getId()).collect(Collectors.toList());
                    sendAuthProcess(String.valueOf(0), AuthTypeConstant.AUTH_CANCEL);
                    cancelAuth(allDeviceIds, collect, validDate);
                    // 调用融合通信删除设备能力授权缓存
                    clearAvcsCache(allDeviceIds);
                } catch (CommonException e) {
                    webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(e.getDispatchMessageList(), null));
                    webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
                    throw e;
                } catch (Exception e) {
                    log.error(AuthExceptionCode.AT206.getCode() + AuthExceptionCode.AT206.getMessage() + e, e);
                    sendError(AuthExceptionCode.AT206);
                } finally {
                    deleteCache();
                }
            });
        }
    }

    @Override
    @AvcsServiceLockAno(group = RedisKey.DEVICE_AUTH_LOCAL)
    public void cancelDeviceAuthAll() {
        // 1.获取所有的角色
        List<String> allRoleIds = getAllRoleIds();
        // 2.获取所有设备
        if (cacheService.hasKey(RedisKey.DEVICE_AUTH_LOCAL)) {
            throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT205));
        } else {
            if (CollectionUtils.isEmpty(allRoleIds)) {
                throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT307));
            }
            executorService.execute(() -> {
                DeviceIdsAndNumParamDTO deviceIdsAndNumParamDTO = new DeviceIdsAndNumParamDTO();
                deviceIdsAndNumParamDTO.setRoleIds(allRoleIds);
                deviceIdsAndNumParamDTO.setDeviceQueryType(DeviceQueryTypeEnum.ALL.getType());
                deviceIdsAndNumParamDTO.setSearchIds(true);
                // 根据所有角色id查出当前类型已授权的设备
                DevicesAndNumDTO devicesAndNumDTO = commonAdcApiService.queryDevicesAndNum(deviceIdsAndNumParamDTO);
                Set<SimpleAdcDeviceDTO> devicces = devicesAndNumDTO.getDevices();
                if (CollectionUtils.isEmpty(devicces)) {
                    return;
                }
                List<String> allDeviceIds = devicces.parallelStream().map(device -> device.getId()).collect(Collectors.toList());
                try {
                    cacheService.set(RedisKey.DEVICE_AUTH_LOCAL, "");
                    sendAuthProcess(String.valueOf(0), AuthTypeConstant.AUTH_CANCEL);
                    cancelAuth(allDeviceIds, allRoleIds, null);
                    // String cancleCode = secretKeyService.cancelActivation();
                    // webSocketService.sendWebSocket(null, AuthCenterConstant.authCancelCode, cancleCode);
                } catch (CommonException e) {
                    webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(e.getDispatchMessageList(), null));
                    webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
                    throw e;
                } catch (Exception e) {
                    log.error(AuthExceptionCode.AT206.getCode() + AuthExceptionCode.AT206.getMessage() + e, e);
                    sendError(AuthExceptionCode.AT206);
                } finally {
                    deleteCache();
                }
            });
        }
    }


    @Override
    public List<String> getAuthDates() {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, DetailDTO> stringDetailDTOEntry : cacheLicenseMap.entrySet()) {
            String key = stringDetailDTOEntry.getKey();
            DetailDTO detailDTO = stringDetailDTOEntry.getValue();
            String endDate = detailDTO.getEndDate();
            list.add(endDate);
        }

        return list;
    }

    @Override
    @AvcsServiceLockAno(group = RedisKey.DEVICE_AUTH_LOCAL)
    public void authByInvalidDevice(AuthInvalidDeviceVO authInvalidDeviceVO) {
        if (cacheService.hasKey(RedisKey.DEVICE_AUTH_LOCAL)) {
            sendError(AuthExceptionCode.AT205);
        } else {
            executorService.execute(() -> {
                try {
                    cacheService.set(RedisKey.DEVICE_AUTH_LOCAL, "");
                    if (CollectionUtils.isEmpty(authInvalidDeviceVO.getDevices())) {
                        sendError(AuthExceptionCode.AT201);
                    }
                    if (StringUtils.isEmpty(authInvalidDeviceVO.getNewValidDate())) {
                        sendError(AuthExceptionCode.AT209);
                    }
                    authInvlidateDevices(authInvalidDeviceVO);
                } catch (CommonException e) {
                    webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(e.getDispatchMessageList(), null));
                    webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
                    throw e;
                } catch (Exception e) {
                    log.error(AuthExceptionCode.AT206.getCode() + AuthExceptionCode.AT206.getMessage() + e, e);
                    sendError(AuthExceptionCode.AT206);
                } finally {
                    deleteCache();
                }
            });
        }
    }

    private void authInvlidateDevices(AuthInvalidDeviceVO authInvalidDeviceVO) {
        sendAuthProcess(String.valueOf(0), AuthTypeConstant.AUTH_UPDATE);
        // 构建删除设备参数；过滤掉新有效期下已授权设备
        RoleValidDateUpdateReqVO roleValidDateUpdateReqVO = createDelDeviceParam(authInvalidDeviceVO
                .getDevices(), authInvalidDeviceVO.getNewValidDate());
        // 数量校验
        checkDevicesCapacity(authInvalidDeviceVO.getNewValidDate(), authInvalidDeviceVO.getDevices());
        // 删除过期设备
        List<List<ValidDateUpdateItemVO>> partition = ListUtils.partition(roleValidDateUpdateReqVO.getUpdateItemVOs(), size);
        for (int i = 0; i < partition.size(); i++) {
            List<ValidDateUpdateItemVO> validDateUpdateItemVOS = partition.get(i);
            RoleValidDateUpdateReqVO vo = new RoleValidDateUpdateReqVO();
            vo.setOperateType(DeviceOperateTypeEnum.DEL.getType());
            vo.setUpdateItemVOs(validDateUpdateItemVOS);
            commonAdcApiService.cancelDeviceAuth(vo);
            float percent = (float) (i + 1) / ((float) (partition.size()) * 2);
            sendAuthProcess(String.valueOf(percent * 100), AuthTypeConstant.AUTH_UPDATE);
        }
        // 新授权
        AuthDeviceDTO authDeviceDTO = new AuthDeviceDTO();
        BeanUtils.copyProperties(authInvalidDeviceVO, authDeviceDTO);
        authInvaliDevice(authDeviceDTO);
    }

    // 删除过期授权
    private RoleValidDateUpdateReqVO createDelDeviceParam(List<DeviceDTO> devices, String fileterDate) {
        fileterDate = fileterDate.replaceAll("[-+,:+,\\s]", "");
        RoleValidDateUpdateReqVO roleValidDateUpdateReqVO = new RoleValidDateUpdateReqVO();
        roleValidDateUpdateReqVO.setOperateType(DeviceOperateTypeEnum.DEL.getType());
        List<ValidDateUpdateItemVO> updateItemVOs = new ArrayList<>();
        Iterator<DeviceDTO> iterator = devices.iterator();
        while (iterator.hasNext()) {
            DeviceDTO device = iterator.next();
            for (ValidDateItemDTO validDateItem : device.getValidDateItems()) {
                String validDate = validDateItem.getValidDate();
                if (fileterDate.equals(validDate)) {
                    iterator.remove();
                    continue;
                }
                ValidDateUpdateItemVO validDateUpdateItemVO = new ValidDateUpdateItemVO();
                validDateUpdateItemVO.setRoleId(validDateItem.getRoleId());
                validDateUpdateItemVO.setDeviceId(device.getId());
                validDateUpdateItemVO.setOldValidDate(validDate);
                updateItemVOs.add(validDateUpdateItemVO);
            }
        }
        roleValidDateUpdateReqVO.setUpdateItemVOs(updateItemVOs);
        return roleValidDateUpdateReqVO;

    }

    @Override
    @AvcsServiceLockAno(group = RedisKey.DEVICE_AUTH_LOCAL)
    public void authByInvalidDeviceAll(AuthInvalidDeviceAllVO authInvalidDeviceAllVO) {
        if (cacheService.hasKey(RedisKey.DEVICE_AUTH_LOCAL)) {
            sendError(AuthExceptionCode.AT205);
        } else {
            executorService.execute(() -> {
                try {
                    cacheService.set(RedisKey.DEVICE_AUTH_LOCAL, "");
                    AuthDevicePageDTO authDevicePageDTO = new AuthDevicePageDTO();
                    BeanUtils.copyProperties(authInvalidDeviceAllVO, authDevicePageDTO);
                    authDevicePageDTO.setPageNo(0);
                    authDevicePageDTO.setPageSize(1000);
                    long totlaPage = 0;
                    long pageNo = 0;
                    List<DeviceDTO> deviceDTOS = new ArrayList<>();
                    do {
                        authDevicePageDTO.setPageNo((int) pageNo);
                        PagerResult<SimpleAdcDeviceDTO> result = getDevicesByExpDate(authDevicePageDTO);
                        totlaPage = result.getTotalPages();
                        PagerResult<DeviceDTO> pagerResult = result.convertTo(simpleAdcDeviceDTO -> {
                            DeviceDTO deviceDTO = new DeviceDTO();
                            BeanUtils.copyProperties(simpleAdcDeviceDTO, deviceDTO);
                            return deviceDTO;
                        });
                        deviceDTOS.addAll(pagerResult.getData());
                        pageNo++;
                    } while (pageNo < totlaPage);
                    if (CollectionUtils.isEmpty(deviceDTOS)) {
                        sendError(AuthExceptionCode.AT309);
                    }
                    AuthInvalidDeviceVO authInvalidDeviceVO = new AuthInvalidDeviceVO();
                    authInvalidDeviceVO.setNewValidDate(authInvalidDeviceAllVO.getNewValidDate());
                    authInvalidDeviceVO.setDevices(deviceDTOS);
                    authInvlidateDevices(authInvalidDeviceVO);
                } catch (CommonException e) {
                    webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(e.getDispatchMessageList(), null));
                    webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
                    throw e;
                } catch (Exception e) {
                    log.error(AuthExceptionCode.AT206.getCode() + AuthExceptionCode.AT206.getMessage() + e, e);
                    sendError(AuthExceptionCode.AT206);
                } finally {
                    deleteCache();
                }
            });
        }
    }

    @Override
    public List<AuthDeviceTypeDTO> getAllDeviceTypeList() {
        List<AuthDeviceTypeDTO> list = new ArrayList<>();
        List<AvcsDeviceType> avcsDeviceTypes = avcsDeviceTypeDao.findAll();
        for (AvcsDeviceType avcsDeviceType : avcsDeviceTypes) {
            String s = avcsDeviceType.getDeviceType();
            AuthDeviceTypeDTO dto = new AuthDeviceTypeDTO();
            dto.setType(s);
            dto.setName(avcsDeviceType.getDeviceTypeName());
            list.add(dto);
        }
        return list;
    }

    @Override
    public PagerResult<SimpleAdcDeviceDTO> getAuthorizedDevice(AuthDevicePageDTO authDevicePageDTO) {
        DevicePageParamDTO dto = new DevicePageParamDTO();
        BeanUtils.copyProperties(authDevicePageDTO, dto);
        if (CollectionUtils.isNotEmpty(dto.getRoleIds())) {
            dto.setWithRole(true);
        }
        if (StringUtils.isNotEmpty(authDevicePageDTO.getValidDate())) {
            dto.setDeviceQueryType(DeviceQueryTypeEnum.DATE.getType());
            String validDate = authDevicePageDTO.getValidDate().replaceAll("[-+,:+,\\s]", "");
            dto.setValidDate(validDate);
        } else {
            dto.setDeviceQueryType(DeviceQueryTypeEnum.ALL.getType());
        }
        return commonAdcApiService.pageDevice(dto);
    }

    @Override
    public PagerResult<SimpleAdcDeviceDTO> getDevicesByExpDate(AuthDevicePageDTO authDevicePageDTO) {
        DevicePageParamDTO dto = new DevicePageParamDTO();
        BeanUtils.copyProperties(authDevicePageDTO, dto);
        if (CollectionUtils.isNotEmpty(dto.getRoleIds())) {
            dto.setWithRole(true);
        }
        dto.setDeviceQueryType(DeviceQueryTypeEnum.INVALID.getType());
        PagerResult<SimpleAdcDeviceDTO> devices = commonAdcApiService.pageDevice(dto);
        List<SimpleAdcDeviceDTO> data = devices.getData();
        // 设置设备过期时间
        if (CollectionUtils.isNotEmpty(data)) {
            long currentTime = System.currentTimeMillis();
            for (SimpleAdcDeviceDTO datum : data) {
                String validDate = datum.getValidDate();
                if (StringUtils.isNotEmpty(validDate)) {
                    String[] dates = validDate.split(",");
                    List<String> ds = Arrays.stream(dates).filter(d -> {
                        long pretime = DateUtils.parseDate(d, new SimpleDateFormat(DateConstant.LONG_DATE_ZMS)).getTime();
                        return (pretime - currentTime) <= 0L ? true : false;
                    }).collect(Collectors.toList());
                    datum.setValidDate(String.join(",", ds));
                }
            }
        }
        return devices;
    }

    @Override
    public Long getExpiredTotal(Integer paltformType) {
        List<String> roles = new ArrayList<>();
        if (0 == paltformType) {
            // 融合角色
            List<String> allRoleIds = getPlatformRoles(true);
            roles.addAll(allRoleIds);
        } else if (1 == paltformType) {
            // 开放平台角色
            List<ApiKeyInfoDTO> apiKeyInfs = getAllApiKey();
            if (CollectionUtils.isNotEmpty(apiKeyInfs)) {
                roles.addAll(apiKeyInfs.stream().map(apiKey -> apiKey.getApiKey()).collect(Collectors.toList()));
            }
            // 轻服务角色
            List<String> allRoleIds = getPlatformRoles(false);
            roles.addAll(allRoleIds);
        } else if (2 == paltformType) {
            roles.addAll(getAllRoleIds());
        }
        if (roles.isEmpty()) {
            return 0L;
        }
        AuthDevicePageDTO authDevicePageDTO = new AuthDevicePageDTO();
        authDevicePageDTO.setRoleIds(roles);
        authDevicePageDTO.setPageNo(0);
        authDevicePageDTO.setPageSize(1);
        PagerResult<SimpleAdcDeviceDTO> result = getDevicesByExpDate(authDevicePageDTO);
        return result.getTotal();
    }


    @Override
    public List<ApiKeyInfoDTO> getAllApiKey() {
        try {
            //LatestModelDTO micoModel = secretKeyService.getModelDetail(ModelEnum.RCS001.getCode());
            //兼容轻服务增值模块
            LatestFixedServerDTO micoModel= secretKeyService.getFixedServerDetail(ActiveCodingConstants.FIXED_SERVER_DISPATCH_MICOSERVICE);
            Boolean isExpire = false;
            if (Objects.nonNull(micoModel)) {
                isExpire = micoModel.getIsExpire();
            }
            if (kiopEnabled && !isExpire) {
                return kiopFeignClient.listApiKey().getResult();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(AuthExceptionCode.AT206.getCode() + AuthExceptionCode.AT206.getMessage() + e, e);
            throw CommonException.warning(DispatchMessage.message(AuthExceptionCode.AT401), null);
        }
    }


    @Override
    public String getOperationProgress() {
        return cacheService.hasKey(RedisKey.AUTH_PROGRESS_BAR_PERCENT) ? (String) cacheService.get(RedisKey.AUTH_PROGRESS_BAR_PERCENT) : null;
    }


    @Override
    public Map<String, Long> getDeviceNumByGroup(QueryNumByGroupDTO queryNumByGroupDTO) {
        if (CollectionUtils.isEmpty(queryNumByGroupDTO.getGroupIds())) {
            return null;
        }
        Map<String, Long> map = new HashMap<>();
        for (String groupId : queryNumByGroupDTO.getGroupIds()) {
            DeviceIdsAndNumParamDTO deviceIdsAndNumParamDTO = new DeviceIdsAndNumParamDTO();
            deviceIdsAndNumParamDTO.setDeviceTypes(Collections.singletonList(queryNumByGroupDTO.getDeviceType()));
            deviceIdsAndNumParamDTO.setDeviceGroupIds(Collections.singletonList(groupId));
            deviceIdsAndNumParamDTO.setDeviceQueryType(DeviceQueryTypeEnum.ALL.getType());
            DevicesAndNumDTO devicesAndNumDTO = commonAdcApiService.queryDevicesAndNumByPage(deviceIdsAndNumParamDTO, 0, 1);
            map.put(groupId, devicesAndNumDTO.getDeviceNum());
        }
        return map;
    }

    private void cancelAuth(List<String> deviceIds, List<String> roleIds, String validDate) {
        List<List<String>> partition = ListUtils.partition(deviceIds, size);
        if (StringUtils.isNotEmpty(validDate)) {
            validDate = validDate.replaceAll("[-+,:+,\\s]", "");
        }
        int total = 0;
        for (List<String> list : partition) {
            total += list.size();
            RoleValidDateUpdateReqVO roleValidDateUpdateReqVO = new RoleValidDateUpdateReqVO();
            roleValidDateUpdateReqVO.setOperateType(DeviceOperateTypeEnum.DEL.getType());
            List<ValidDateUpdateItemVO> updateItemVOs = new ArrayList<>();
            for (String deviceId : list) {
                for (String roleId : roleIds) {
                    ValidDateUpdateItemVO validDateUpdateItemVO = new ValidDateUpdateItemVO();
                    validDateUpdateItemVO.setRoleId(roleId);
                    validDateUpdateItemVO.setDeviceId(deviceId);
                    if (StringUtils.isNotEmpty(validDate)) {
                        validDateUpdateItemVO.setOldValidDate(validDate);
                    }
                    updateItemVOs.add(validDateUpdateItemVO);
                }
            }
            roleValidDateUpdateReqVO.setUpdateItemVOs(updateItemVOs);
            commonAdcApiService.cancelDeviceAuth(roleValidDateUpdateReqVO);
            float percent = (float) total / (float) deviceIds.size();
            sleep(percent);
            sendAuthProcess(String.valueOf(percent * 100), AuthTypeConstant.AUTH_CANCEL);
        }
    }


    //校验参数进行去重
    private AuthDeviceDTO checkParam(AuthDeviceDTO authDeviceDTO) {
        List<DeviceDTO> deviceIds = authDeviceDTO.getDevices();
        if (CollectionUtils.isEmpty(deviceIds)) {
            sendError(AuthExceptionCode.AT201);
        }
        List<DeviceDTO> distinctDeviceList = deviceIds.stream().distinct().collect(Collectors.toList());
        authDeviceDTO.setDevices(distinctDeviceList);
        return authDeviceDTO;
    }

    @Override
    public Integer countUsedDevice(List<String> deviceTypes, List<String> allRoleIds, String
            validate) {
        if (CollectionUtils.isEmpty(allRoleIds)) {
            return 0;
        }
        int deviceNum = 0;
        DeviceIdsAndNumParamDTO deviceIdsAndNumParamDTO = new DeviceIdsAndNumParamDTO();
        deviceIdsAndNumParamDTO.setRoleIds(allRoleIds);
        deviceIdsAndNumParamDTO.setDeviceTypes(deviceTypes);
        validate = validate.replaceAll("[-+,:+,\\s]", "");
        deviceIdsAndNumParamDTO.setValidDate(validate);
        deviceIdsAndNumParamDTO.setDeviceQueryType(DeviceQueryTypeEnum.DATE.getType());
        DevicesAndNumDTO deviceIdsAndNum = commonAdcApiService.queryDevicesAndNumByPage(deviceIdsAndNumParamDTO, 0, 1);
        if (null != deviceIdsAndNum.getDeviceNum() && 0L != deviceIdsAndNum.getDeviceNum()) {
            deviceNum += deviceIdsAndNum.getDeviceNum();
        }
        return deviceNum;
    }


    private boolean checkCapiticyByGroup(int usedDeviceCount, int deviceCount, DetailDTO
            codeInfo, Collection<DispatchMessage> dispatchMessageList,
                                         String terminalDeviceType, DeviceCategoryEnum deviceTypeEnum) {
        if (!terminalDeviceType.isEmpty()) {
            Integer amount = getAmountByDeviceType(codeInfo, deviceTypeEnum);
            if (amount != -1) {
                if ((usedDeviceCount + deviceCount) <= amount) {
                    return false;
                }
                long usedNum = deviceCount + usedDeviceCount;
                if (amount - usedNum < 0) {
                    String msg = AuthExceptionCode.AT202.getMessage().replace("{deviceType}",
                            deviceTypeEnum.getDesc()).replace("{num}", String.valueOf(usedNum - amount));
                    dispatchMessageList.add(DispatchMessage.message(new AuthCenterMsg(AuthExceptionCode.AT202.getCode(), msg)));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @trurn boolean  是否超出容量
     */
    private boolean checkCapiticy(List<String> deviceIds, DetailDTO codeInfo, List<String> allRoleIds,
                                  Map<String, Set<String>> aMemSetMap, Collection<DispatchMessage> dispatchMessageList,
                                  String terminalDeviceType, DeviceCategoryEnum deviceTypeEnum, List<String> deviceTypes) {
        if (!terminalDeviceType.isEmpty()) {
            Integer amount = getAmountByDeviceType(codeInfo, deviceTypeEnum);
            if (amount != -1) {
                terminalDeviceType = terminalDeviceType.substring(0, terminalDeviceType.length() - 1);
                int usedCount = getUsedCount(codeInfo, allRoleIds, aMemSetMap, deviceTypeEnum, deviceTypes);
                // 优化：数量足够的条件下，避免查库去重校验
                if ((usedCount + deviceIds.size()) <= amount) {
                    return false;
                }
                DeviceIdsAndNumParamDTO deviceIdsAndNumParamDTO = new DeviceIdsAndNumParamDTO();
                deviceIdsAndNumParamDTO.setRoleIds(allRoleIds);
                deviceIdsAndNumParamDTO.setDeviceTypes(Arrays.asList(terminalDeviceType.split(",")));
                deviceIdsAndNumParamDTO.setSearchIds(true);
                String endDate = codeInfo.getEndDate().replaceAll("[-+,:+,\\s]", "");
                deviceIdsAndNumParamDTO.setValidDate(endDate);
                deviceIdsAndNumParamDTO.setDeviceQueryType(DeviceQueryTypeEnum.DATE.getType());
                // 根据所有角色id查出当前类型已授权的设备
                DevicesAndNumDTO deviceIdsAndNumDTO = commonAdcApiService.queryDevicesAndNum(deviceIdsAndNumParamDTO);
                Set<SimpleAdcDeviceDTO> allDeviceIds = deviceIdsAndNumDTO.getDevices();
                // 去除交集
                if (CollectionUtils.isNotEmpty(allDeviceIds)) {
                    deviceIds = deviceIds.parallelStream().filter(s -> allDeviceIds.parallelStream().map(device -> device.getId())
                            .noneMatch(s::equals)).collect(Collectors.toList());
                }
                int sizeCollect = deviceIds.size();
                long usedNum = sizeCollect + usedCount;
                if (amount - usedNum < 0) {
                    String msg = AuthExceptionCode.AT202.getMessage().replace("{deviceType}",
                            deviceTypeEnum.getDesc()).replace("{num}", String.valueOf(usedNum - amount));
                    dispatchMessageList.add(DispatchMessage.message(new AuthCenterMsg(AuthExceptionCode.AT202.getCode(), msg)));
                    return true;
                }
            }
        }
        return false;
    }

    private int getUsedCount(DetailDTO
                                     codeInfo, List<String> allRoleIds, Map<String, Set<String>> aMemSetMap, DeviceCategoryEnum
                                     deviceTypeEnum, List<String> deviceTypes) {
        Integer count = 0;


        switch (deviceTypeEnum) {
            case IPC:
                return countUsedDevice(deviceTypes, allRoleIds, codeInfo.getEndDate());
            case TERMINAL:
                count = countUsedDevice(deviceTypes, allRoleIds, codeInfo.getEndDate());
                int tempSize = 0;
                if (Objects.nonNull(aMemSetMap.get(MemberTypeEnum.TEMP_MT.getValue()))) {
                    tempSize = aMemSetMap.get(MemberTypeEnum.TEMP_MT.getValue()).size();
                }
                return tempSize + count;
            case TELEPHONE:
                count = countUsedDevice(deviceTypes, allRoleIds, codeInfo.getEndDate());
                int tempSize1 = 0;
                if (Objects.nonNull(aMemSetMap.get(MemberTypeEnum.TEMP_MOBILEPHONE.getValue()))) {
                    tempSize1 = aMemSetMap.get(MemberTypeEnum.TEMP_MOBILEPHONE.getValue()).size();
                }
                return count + tempSize1;
            case PTT:
                count = countUsedDevice(deviceTypes, allRoleIds, codeInfo.getEndDate());
                int tempSize2 = 0;
                if (Objects.nonNull(aMemSetMap.get(MemberTypeEnum.TEMP_PTT.getValue()))) {
                    tempSize2 = aMemSetMap.get(MemberTypeEnum.TEMP_PTT.getValue()).size();
                }
                return count + tempSize2;
            case APP:
                count = countUsedDevice(deviceTypes, allRoleIds, codeInfo.getEndDate());
                int tempSize3 = 0;
                if (Objects.nonNull(aMemSetMap.get(MemberTypeEnum.APP.getValue()))) {
                    tempSize3 = aMemSetMap.get(MemberTypeEnum.APP.getValue()).size();
                }
                return count + tempSize3;

        }
        return count;
    }

    private Integer getAmountByDeviceType(DetailDTO codeInfo, DeviceCategoryEnum deviceTypeEnum) {
        switch (deviceTypeEnum) {
            case IPC:
                return codeInfo.getIpcAmount();
            case TERMINAL:
                return codeInfo.getTerminalAmount();
            case TELEPHONE:
                return codeInfo.getTelephoneAmount();
            case PTT:
                return codeInfo.getPttAmount();
            case APP:
                return codeInfo.getAppAmount();
        }
        return -1;
    }

    // 设备权限异步删除数据，导致授权日期变更，取消操作后页面展示内容错误。故延迟三秒，再让前端刷新页面
    private void sleep(Float precent) {
        if (precent >= 1l && sleepEnable) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void sendError(AuthExceptionCode code) {
        List<DispatchMessage> dispatchMessageList = new ArrayList<>();
        dispatchMessageList.add(DispatchMessage.message(code));
        webSocketService.sendWebSocket(null, "/message", DispatchResponseMessage.error(dispatchMessageList, null));
        webSocketService.sendWebSocket(null, AuthCenterConstant.interfaceStatus, JSON.toJSONString(new InterfaceStatusDTO("-1")));
        throw CommonException.error(DispatchMessage.message(code));
    }
}
