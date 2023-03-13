package com.kedacom.dispatch.ac.web.service.impl;

import com.kedacom.dispatch.ac.data.dao.RMemDAO;
import com.kedacom.dispatch.ac.data.entity.RMem;
import com.kedacom.dispatch.ac.data.exception.AuthExceptionCode;
import com.kedacom.dispatch.ac.data.vo.MemVO;
import com.kedacom.dispatch.ac.data.vo.RMemVO;
import com.kedacom.dispatch.ac.web.service.AMemService;
import com.kedacom.dispatch.ac.web.service.RMemService;
import com.kedacom.dispatch.common.data.exception.CommonException;
import com.kedacom.dispatch.common.data.exception.DispatchMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

/**
 * @author : liuyue
 * @Description :
 * @date : 2021/11/8 17:26
 */
@Service
@Slf4j
@Transactional
public class RMemServiceImpl implements RMemService {

    @Autowired
    private RMemDAO rMemDAO;

    @Autowired
    private AMemService aMemService;

    /**
     * 根据用户角色授权设备
     *
     * @param rMemVOList List<RMemVO>
     * @return List<RMemVO>
     */
    @Override
    public List<RMemVO> addRMem(List<RMemVO> rMemVOList) {
        validationRMemVOs(rMemVOList);

        Map<String, Set<String>> toCheckDevices = rMemVOList.stream().
                collect(Collectors.groupingBy(x -> x.getMemTy().toLowerCase(), Collectors.mapping(RMemVO::getMemId, toSet())));

//        Map<String, Set<String>> savedDevices = aMemService.searchAllDevices();
//
//        aMemService.checkoutCapacity(toCheckDevices, savedDevices);
//
//        List<RMemVO> rMemVOS = removeDupAuth(rMemVOList);
//
//        if (rMemVOS.size() > 0) {
//            List<RMem> rMemList = rMemVOS.stream().map(RMem::new).collect(Collectors.toList());
//            rMemDAO.saveAll(rMemList);
//        }
//
//        return rMemVOS;
        return null;
    }

    /**
     * 根据角色查询授权设备
     *
     * @param rIds List<String> 用户角色列表
     * @return Map<String, List < MemVO>>  以用户角色id作为key,设备列表为value
     */
    @Override
    public Map<String, List<MemVO>> searchMemByR(List<String> rIds) {
        Map<String, List<MemVO>> roleAuthDevices = new HashMap<>();

        rIds.forEach(rId -> {
            List<RMem> allByrId = rMemDAO.findAllByRoleId(rId);
            if (CollectionUtils.isNotEmpty(allByrId)) {
                List<MemVO> devices = new ArrayList<>();

                allByrId.forEach(item -> {
                    MemVO memVO = new MemVO(item);
                    devices.add(memVO);
                });

                roleAuthDevices.put(rId, devices);
            }
        });
        return roleAuthDevices;
    }

    /**
     * 根据用户角色id删除记录
     *
     * @param rIds
     */
    @Override
    public void deleteByR(List<String> rIds) {
        if (rIds.size() > 0) {
            rMemDAO.deleteByRoleIdIn(rIds);
        }
    }

    /**
     * 验证数据是否都存在以及设备类型是否正确
     *
     * @param rMemVOList List<RMemVO>
     */
    void validationRMemVOs(List<RMemVO> rMemVOList) {
        if (rMemVOList != null) {
            for (RMemVO rMemVO : rMemVOList) {
                if (rMemVO.getRoleId() == null) {
                    throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT103));
                }
                if (rMemVO.getMemId() == null) {
                    throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT100));
                }
                if (rMemVO.getMemTy() == null) {
                    throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT101));
                } else if (!aMemService.checkTmpDeviceType(rMemVO.getMemTy())) {
                    // 验证设备类型是否正确
                    throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT104));
                }
            }
        } else {
            throw CommonException.error(DispatchMessage.message(AuthExceptionCode.AT106));
        }
    }

    /**
     * 去除重复的数据
     *
     * @param rMemVOList List<RMemVO>
     * @return List<RMemVO>
     */
    @Override
    public List<RMemVO> removeDupAuth(List<RMemVO> rMemVOList) {

        Set<RMemVO> aMemVOS = new HashSet<>(rMemVOList);

        List<RMemVO> removedList = new ArrayList<>(aMemVOS);

        List<RMem> all = rMemDAO.findAll();

        removedList = removedList.stream().filter(rMemVO ->
                all.stream().noneMatch(rMem -> rMemVO.equals(new RMemVO(rMem)))).collect(Collectors.toList());

        return removedList;
    }
}
