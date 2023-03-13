package com.kedacom.dispatch.ac.web.service.impl;

import com.kedacom.avcs.dispatch.manage.data.dto.DetailDTO;
import com.kedacom.avcs.dispatch.secretkey.web.service.SecretKeyService;
import com.kedacom.dispatch.ac.data.dao.AMemDAO;
import com.kedacom.dispatch.ac.data.dao.RMemDAO;
import com.kedacom.dispatch.ac.data.dto.AuthCenterMsg;
import com.kedacom.dispatch.ac.data.entity.AMem;
import com.kedacom.dispatch.ac.data.entity.RMem;
import com.kedacom.dispatch.ac.data.exception.AuthExceptionCode;
import com.kedacom.dispatch.ac.data.vo.AMemVO;
import com.kedacom.dispatch.ac.data.vo.MemVO;
import com.kedacom.dispatch.ac.web.service.AMemService;
import com.kedacom.dispatch.common.data.constant.MemberTypeEnum;
import com.kedacom.dispatch.common.data.exception.CommonException;
import com.kedacom.dispatch.common.data.exception.DispatchMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

/**
 * @author : liuyue
 * @Description :
 * @date : 2021/11/10 9:49
 */
@Service
@Slf4j
@Transactional
public class AMemServiceImpl implements AMemService {

    @Autowired
    private AMemDAO aMemDAO;

    @Autowired
    private RMemDAO rMemDAO;

    @Autowired
    private SecretKeyService secretKeyService;


    /**
     * 根据APIKey授权设备
     *
     * @param
     * @return AMemVO
     */
    @Override
    public List<AMemVO> addAMem(List<AMemVO> aMemVOList,String validate) {
        validationAMemVOs(aMemVOList);

        // 将参数按照设备类型分类去重
        Map<String, Set<String>> toCheckDevices = aMemVOList.stream().
                collect(Collectors.groupingBy(x -> x.getMemTy().toLowerCase(), Collectors.mapping(AMemVO::getMemId, toSet())));

        Map<String, Set<String>> savedDevices = searchAllDevices();

        checkoutCapacity(toCheckDevices, savedDevices,validate);

        List<AMemVO> aMemVOS = removeDupAuth(aMemVOList);
        if (aMemVOS.size() > 0) {
            List<AMem> aMemList = aMemVOS.stream().map(AMem::new).collect(Collectors.toList());
            aMemDAO.saveAll(aMemList);
        }

        return aMemVOS;
    }

    /**
     * 查询所有APIkey授权的设备
     *
     * @return Map<String, List < MemVO>>   以APIKey为key
     */
    @Override
    public Map<String, List<MemVO>> searchAllAMem() {

        List<AMem> all = aMemDAO.findAll();
        HashMap<String, List<MemVO>> aMemMap = new HashMap<>();

        // 将查询到的结果处理成map格式key是apiKey,value是设备列表
        for (AMem aMem : all) {
            if (!aMemMap.containsKey(aMem.getApiId())) {
                List<MemVO> memVOS = new ArrayList<>();
                memVOS.add(new MemVO(aMem));
                aMemMap.put(aMem.getApiId(), memVOS);
            } else {
                aMemMap.get(aMem.getApiId()).add(new MemVO(aMem));
            }
        }

        return aMemMap;
    }

    /**
     * 根据用户角色删除所有的记录
     *
     * @param aIds List<String>
     */
    @Override
    public void deleteByA(List<String> aIds) {
        if (aIds.size() > 0) {
            aMemDAO.deleteByApiIdIn(aIds);
        }
    }

    /**
     * 讲两个授权设备表的数据都取出来，处理成map格式，key是设备类型，value是设备集合，设备去重
     *
     * @return Map<String, Set < String>>
     */
    public Map<String, Set<String>> searchAllDevices() {
        List<RMem> allDevicesByRoles = rMemDAO.findAll();
        List<AMem> allDevicesByApiKeys = aMemDAO.findAll();

        if (allDevicesByApiKeys != null) {
            List<RMem> collect = allDevicesByApiKeys.stream().map(aMem -> {
                RMem rMem = new RMem();
                rMem.setMemId(aMem.getMemId());
                rMem.setMemTy(aMem.getMemTy());
                return rMem;
            }).collect(Collectors.toList());

            allDevicesByRoles.addAll(collect);
        }
        if (allDevicesByRoles != null) {
            return allDevicesByRoles.stream().
                    collect(Collectors.groupingBy(x -> x.getMemTy().toLowerCase(), Collectors.mapping(RMem::getMemId, toSet())));
        } else {
            return null;
        }

    }

    /**
     * 验证数据是否都存在以及设备类型是否正确
     *
     * @param aMemVOList AMemVO
     */
    void validationAMemVOs(List<AMemVO> aMemVOList) {
        if (aMemVOList != null) {
            for (AMemVO aMemVO : aMemVOList) {
                if (aMemVO.getApiId() == null) {
                    throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT102));
                }
                if (aMemVO.getMemId() == null) {
                    throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT101));
                }
                if (aMemVO.getMemTy() == null) {
                    throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT100));
                } else if (!checkTmpDeviceType(aMemVO.getMemTy())) {
                    throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT104));
                }
            }
        } else {
            throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT106));
        }
    }

    /**
     * 检查设备类型是否是TEMP_KEDACHAT,TEMP_MOBILEPHONE,TEMP_PTT,TEMP_MT
     *
     * @param deviceType String
     * @return Boolean
     */
    public Boolean checkTmpDeviceType(String deviceType) {
        return deviceType.equalsIgnoreCase(MemberTypeEnum.TEMP_KEDACHAT.getValue()) ||
                (deviceType.equalsIgnoreCase(MemberTypeEnum.TEMP_MOBILEPHONE.getValue())) ||
                deviceType.equalsIgnoreCase(MemberTypeEnum.TEMP_PTT.getValue()) ||
                (deviceType.equalsIgnoreCase(MemberTypeEnum.TEMP_MT.getValue()));
    }

    /**
     * 检查每种设备类型的设备容量是否足够
     *
     * @param toCheckDevices Map<String, Set<String>>
     * @param savedDevices   Map<String, Set<String>>
     */
    @Override
    public void checkoutCapacity(Map<String, Set<String>> toCheckDevices, Map<String, Set<String>> savedDevices,String validate) {

        // 获取初始设备容量
        Map<String, DetailDTO> detailDTOs = secretKeyService.getCapacityPackage();

        if (null == detailDTOs.get(validate)) {
            throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT303));
        }
        DetailDTO  capacityPackage = detailDTOs.get(validate);
        if (-1 == capacityPackage.getTerminalAmount()) {
            return;
        }

        if (toCheckDevices.get(MemberTypeEnum.TEMP_KEDACHAT.getValue()) != null) {
            // 验证TEMP_KEDACHAT类型设备容量是否足够
            checkoutTypeCapacity(MemberTypeEnum.TEMP_KEDACHAT.getValue(), toCheckDevices, savedDevices, capacityPackage.getAppAmount());
        }
        if (toCheckDevices.get(MemberTypeEnum.TEMP_MT.getValue()) != null) {
            // 验证TEMP_MT类型设备容量是否足够
            checkoutTypeCapacity(MemberTypeEnum.TEMP_MT.getValue(), toCheckDevices, savedDevices, capacityPackage.getTerminalAmount());
        }
        if (toCheckDevices.get(MemberTypeEnum.TEMP_PTT.getValue()) != null) {
            // 验证TEMP_PTT类型设备容量是否足够
            checkoutTypeCapacity(MemberTypeEnum.TEMP_PTT.getValue(), toCheckDevices, savedDevices, capacityPackage.getPttAmount());
        }
        if (toCheckDevices.get(MemberTypeEnum.TEMP_MOBILEPHONE.getValue()) != null) {
            // 验证TEMP_MOBILEPHONE类型设备容量是否足够
            checkoutTypeCapacity(MemberTypeEnum.TEMP_MOBILEPHONE.getValue(), toCheckDevices, savedDevices, capacityPackage.getTelephoneAmount());
        }
    }

    /**
     * 验证某类型设备的容量是否足够
     *
     * @param deviceType     String  设备类型
     * @param toCheckDevices Map<String, Set<String>>
     * @param savedDevices   Map<String, Set<String>> savedDevices
     * @param capacity       Integer  改设备类型的总容量
     */
    void checkoutTypeCapacity(String deviceType, Map<String, Set<String>> toCheckDevices,
                              Map<String, Set<String>> savedDevices,
                              Integer capacity) {
        // 获取待授权deviceType类型设备
        Set<String> checkDevices = toCheckDevices.get(deviceType);
        // 获取数据库中已授权过的deviceType类型设备
        Set<String> savedTypeDevices = savedDevices.get(deviceType);

        if (checkDevices.size() > 0 && savedTypeDevices.size() > 0) {
            // 遍历待授权设备，验证是否已经授权过
            checkDevices.removeIf(savedTypeDevices::contains);
        }

        // todo

        if (capacity - savedDevices.size() < checkDevices.size()) {
            String msg = AuthExceptionCode.AT105.getMessage().replace("{deviceType}", deviceType);
            throw CommonException.error(DispatchMessage.message(new AuthCenterMsg(AuthExceptionCode.AT105.getCode(), msg)));
        }
    }

    /**
     * 去除重复的数据
     *
     * @param aMemVOList List<AMemVO>
     * @return List<AMemVO>
     */
    public List<AMemVO> removeDupAuth(List<AMemVO> aMemVOList) {

        Set<AMemVO> aMemVOS = new HashSet<>(aMemVOList);

        List<AMemVO> removedList = new ArrayList<>(aMemVOS);

        List<AMem> all = aMemDAO.findAll();

        removedList = removedList.stream().filter(aMemVO ->
                all.stream().noneMatch(aMem -> aMemVO.equals(new AMemVO(aMem)))).collect(Collectors.toList());

        return removedList;
    }
}

