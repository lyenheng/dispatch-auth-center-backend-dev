package com.kedacom.dispatch.ac.web.service.impl;

import com.kedacom.dispatch.ac.data.constants.AvcsDeviceTypeConstant;
import com.kedacom.dispatch.ac.data.dao.AvcsDeviceTypeDao;
import com.kedacom.dispatch.ac.data.dto.AuthCenterMsg;
import com.kedacom.dispatch.ac.data.entity.AvcsDeviceType;
import com.kedacom.dispatch.ac.data.exception.AuthExceptionCode;
import com.kedacom.dispatch.ac.data.vo.DeviceTypeReqVO;
import com.kedacom.dispatch.ac.data.vo.DeviceTypeVO;
import com.kedacom.dispatch.ac.web.service.AvcsDeviceTypeService;
import com.kedacom.dispatch.ac.web.service.DeviceAuthService;
import com.kedacom.dispatch.common.adc.service.CommonAdcApiService;
import com.kedacom.dispatch.common.data.constant.DeviceQueryTypeEnum;
import com.kedacom.dispatch.common.data.dto.adc.DeviceIdsAndNumParamDTO;
import com.kedacom.dispatch.common.data.dto.adc.DevicesAndNumDTO;
import com.kedacom.dispatch.common.data.exception.CommonException;
import com.kedacom.dispatch.common.data.exception.DispatchMessage;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/06/07/ 18:32
 */
@Service
@Slf4j
public class AvcsDeviceTypeServiceImpl implements AvcsDeviceTypeService {

    @Autowired
    private AvcsDeviceTypeDao avcsDeviceTypeDao;
    @Autowired
    private DeviceAuthService deviceAuthService;
    @Autowired
    private CommonAdcApiService commonAdcApiService;

    @Override
    public List<AvcsDeviceType> getDeviceTypes() {
        List<AvcsDeviceType> deviceTypes = avcsDeviceTypeDao.findAll();
        return deviceTypes;
    }

    @Override
    @Transactional
    public void add(DeviceTypeReqVO deviceTypeReqVO) {
        List<AvcsDeviceType> deviceTypes = avcsDeviceTypeDao.findAll();
        if (Collections.isEmpty(deviceTypes)) {
            throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT311));
        }
        if (Collections.isEmpty(deviceTypeReqVO.getDeviceTypeVOS())) {
            throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT312));
        }
        List<String> typesDb = deviceTypes.stream().map(d -> d.getDeviceType()).collect(Collectors.toList());
        List<DeviceTypeVO> deviceTypeVOS = deviceTypeReqVO.getDeviceTypeVOS();
        List<String> dbEditNotList = deviceTypes.stream().filter(type -> AvcsDeviceTypeConstant.EDIT_N.equals(type.getIsEdit()))
                .map(s -> s.getDeviceType()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(dbEditNotList)) {
            // 固定类型不允许编辑
            Optional<String> msgOpt = deviceTypeVOS.stream()
                    .filter(s -> dbEditNotList.contains(s.getDeviceType()))
                    .map(vo -> vo.getDeviceType())
                    .reduce((a, b) -> a + "," + b);
            if (msgOpt.isPresent()) {
                String msg = AuthExceptionCode.AT314.getMessage().replace("{deviceType}", msgOpt.get());
                throw CommonException.error(DispatchMessage.message(new AuthCenterMsg(AuthExceptionCode.AT314.getCode(), msg)));
            }
        }
        Map<Boolean, List<DeviceTypeVO>> collect = deviceTypeVOS.stream().collect(Collectors.groupingBy(DeviceTypeVO::getIsSelected));
        List<DeviceTypeVO> toCalTypes = collect.get(false);
        // 删除设备类型
        if (CollectionUtils.isNotEmpty(toCalTypes)) {
            List<DeviceTypeVO> calTypes = toCalTypes.stream().filter(calType -> typesDb.contains(calType.getDeviceType())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(calTypes)) {
                // 禁止取消有授权设备的类型
                List<String> tps = null;
                try {
                    tps = countInvalidTypes(calTypes);
                }catch (Exception e){
                    log.error(AuthExceptionCode.AT402.getMessage() + e.getMessage(), e);
                    throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT402));
                }
                if (CollectionUtils.isNotEmpty(tps)) {
                    String msg = AuthExceptionCode.AT313.getMessage().replace("{deviceType}", tps.stream().reduce((a, b) -> a + "," + b).get());
                    throw CommonException.error(DispatchMessage.message(new AuthCenterMsg(AuthExceptionCode.AT313.getCode(), msg)));
                } else {
                    List<String> collect1 = calTypes.stream().map(s -> s.getDeviceType()).collect(Collectors.toList());
                    avcsDeviceTypeDao.deleteByDeviceTypeIn(collect1);
                }
            }
        }
        // 添加设备类型
        List<DeviceTypeVO> toAddTypes = collect.get(true);
        if (CollectionUtils.isNotEmpty(toAddTypes)) {
            // 过滤已存在的
            List<DeviceTypeVO> addTypes = toAddTypes.stream().filter(s -> !typesDb.contains(s.getDeviceType())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(addTypes)) {
                List<AvcsDeviceType> addList = new ArrayList<>();
                for (DeviceTypeVO addType : addTypes) {
                    AvcsDeviceType avcsDeviceType =new AvcsDeviceType();
                    BeanUtils.copyProperties(addType, avcsDeviceType);
                    avcsDeviceType.setIsEdit(AvcsDeviceTypeConstant.EDIT_Y);
                    addList.add(avcsDeviceType);
                }
                avcsDeviceTypeDao.saveAll(addList);
            }
        }
    }


    // 根据设备类型查询有效设备数量
    private List<String> countInvalidTypes(List<DeviceTypeVO> calTypes) {
        List<String> allRoleIds = deviceAuthService.getAllRoleIds();
        List<String> list = new ArrayList<>();
        for (DeviceTypeVO deviceType : calTypes) {
            DeviceIdsAndNumParamDTO deviceIdsAndNumParamDTO = new DeviceIdsAndNumParamDTO();
            deviceIdsAndNumParamDTO.setRoleIds(allRoleIds);
            deviceIdsAndNumParamDTO.setDeviceTypes(java.util.Collections.singletonList(deviceType.getDeviceType()));
            deviceIdsAndNumParamDTO.setDeviceQueryType(DeviceQueryTypeEnum.VALID.getType());
            DevicesAndNumDTO deviceIdsAndNum = commonAdcApiService.queryDevicesAndNumByPage(deviceIdsAndNumParamDTO, 0, 1);
            if (null != deviceIdsAndNum.getDeviceNum() && deviceIdsAndNum.getDeviceNum() > 0) {
                list.add(deviceType.getDeviceTypeName());
            }
        }
        return list;
    }
}
