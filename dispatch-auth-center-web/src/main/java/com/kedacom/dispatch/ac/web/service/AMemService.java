package com.kedacom.dispatch.ac.web.service;

import com.kedacom.dispatch.ac.data.vo.AMemVO;
import com.kedacom.dispatch.ac.data.vo.MemVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : liuyue
 * @Description :
 * @date : 2021/11/10 9:51
 */
public interface AMemService {
    List<AMemVO> addAMem(List<AMemVO> aMemVOList,String valiDate);

    Map<String, List<MemVO>> searchAllAMem();

    void deleteByA(List<String> aIds);

    Map<String, Set<String>> searchAllDevices();

    Boolean checkTmpDeviceType(String deviceType);

    void checkoutCapacity(Map<String, Set<String>> toCheckDevices, Map<String, Set<String>> savedDevices,String validate);

    List<AMemVO> removeDupAuth(List<AMemVO> aMemVOList);

}
