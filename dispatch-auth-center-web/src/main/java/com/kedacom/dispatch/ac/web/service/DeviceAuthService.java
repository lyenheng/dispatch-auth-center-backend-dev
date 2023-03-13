package com.kedacom.dispatch.ac.web.service;

import com.kedacom.adc.common.dto.device.RaDeviceDTO;
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
import com.kedacom.dispatch.ac.data.vo.AuthInvalidDeviceAllVO;
import com.kedacom.dispatch.ac.data.vo.AuthInvalidDeviceVO;
import com.kedacom.dispatch.ac.data.vo.UnauthDevicesVO;
import com.kedacom.dispatch.common.data.dto.adc.SimpleAdcDeviceDTO;

import java.util.List;
import java.util.Map;

/**
 * @author chenyang
 * @date 2021/11/17 19:42
 */
public interface DeviceAuthService {
    void authByDevice(AuthDeviceDTO authDeviceDTO);

    void authByGroup(AuthGroupDTO authGroupDTO);

    void cancelDeviceAuth(AuthDeviceDTO authDeviceDTO);

    List<AuthDeviceTypeDTO> getAllDeviceTypeList();

    PagerResult<SimpleAdcDeviceDTO> getAuthorizedDevice(AuthDevicePageDTO authDevicePageDTO);

    List<ApiKeyInfoDTO> getAllApiKey();

    String getOperationProgress();

    void cancelDeviceAuthByType(CancelAuthDTO authGroupDTO);

    Map<String, Long> getDeviceNumByGroup(QueryNumByGroupDTO queryNumByGroupDTO);

    List<UsedDeviceAmountDTO> getUsedDeviceAmountByExpTime(String validDate);

    PagerResult<SimpleAdcDeviceDTO> getDevicesByExpDate(AuthDevicePageDTO authDevicePageDTO);

    Long getExpiredTotal(Integer paltformType);

    void updateAuthByDevice(AuthDeviceDTO authDeviceDTO);

    void updateAuthByAllDevice(AuthDeviceDTO authDeviceDTO);

    void cancelDeviceAuthAll();

    List<String> getAuthDates();

    void authByInvalidDevice(AuthInvalidDeviceVO authInvalidDeviceVO);

    void authByInvalidDeviceAll(AuthInvalidDeviceAllVO authInvalidDeviceAllVO);
    // 获取融合平台和请服务的所有角色
    List<String> getAllRoleIds();

    Integer countUsedDevice(List<String> deviceTypes, List<String> allRoleIds, String validate);

    // PagerResult<RaDeviceDTO> getUnAuthDevices(UnauthDevicesVO unauthDevicesVO);

    void deleteCache();

    void cancelDeviceAuthByDate(String date);

    void cancelDeviceAuthByDateAnRoles(RoleDateDTO roleDateDTO);

//    PagerResult<RaDeviceDTO> getCurrentUserAuthDevice(UnauthDevicesVO unauthDevicesVO);
//    void cancelAuthByGroup(AuthGroupDTO authGroupDTO);
}
