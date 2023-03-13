package com.kedacom.dispatch.ac.web.thread;

import com.kedacom.avcs.dispatch.manage.data.dto.DetailDTO;
import com.kedacom.dispatch.ac.data.dto.UsedDeviceAmountDTO;
import com.kedacom.dispatch.ac.data.entity.AvcsDeviceType;
import com.kedacom.dispatch.ac.data.enums.DeviceCategoryEnum;
import com.kedacom.dispatch.ac.web.service.DeviceAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @Description:设备数量查询线程
 * @Auther: liuyanhui
 * @Date: 2022/07/11/ 14:50
 */
public class DeviceCountThread implements Callable<UsedDeviceAmountDTO> {
    String validDate;
    List<String> allRoleIds;
    Map<String, DetailDTO>  codeInfo;
    DetailDTO detailDTO;
    Map<String, List<AvcsDeviceType>> collect;
    DeviceCategoryEnum value;
    DeviceAuthService deviceAuthService;
    public DeviceCountThread(String validDate, List<String> allRoleIds,
                             Map<String, DetailDTO>  codeInfo, DetailDTO detailDTO,
                             Map<String, List<AvcsDeviceType>> collect, DeviceCategoryEnum value,DeviceAuthService deviceAuthService) {
        this.validDate = validDate;
        this.allRoleIds = allRoleIds;
        this.codeInfo = codeInfo;
        this.detailDTO = detailDTO;
        this.collect = collect;
        this.value = value;
        this.deviceAuthService =deviceAuthService;
    }

    @Override
    public UsedDeviceAmountDTO call(){
        UsedDeviceAmountDTO usedDeviceAmountDTO = new UsedDeviceAmountDTO();
        List<AvcsDeviceType> avcsDeviceTypes = collect.get(value.getCategory());
        List<String> types = avcsDeviceTypes.stream().map(s -> s.getDeviceType()).collect(Collectors.toList());
        Integer count = deviceAuthService.countUsedDevice(types, allRoleIds, validDate);
        usedDeviceAmountDTO.setDeviceType(value.name());
        if (Objects.isNull(codeInfo) || null == detailDTO) {
            usedDeviceAmountDTO.setUnUsedAmount(0);
            usedDeviceAmountDTO.setInUsedAmount(0);
        } else {
            switch (value) {
                case IPC:
                    setUnUsedAmount(usedDeviceAmountDTO, count, detailDTO.getIpcAmount());
                    break;
                case TERMINAL:
                    setUnUsedAmount(usedDeviceAmountDTO, count, detailDTO.getTerminalAmount());
                    break;
                case TELEPHONE:
                    setUnUsedAmount(usedDeviceAmountDTO, count, detailDTO.getTelephoneAmount());
                    break;
                case PTT:
                    setUnUsedAmount(usedDeviceAmountDTO, count, detailDTO.getPttAmount());
                    break;
                case APP:
                    setUnUsedAmount(usedDeviceAmountDTO, count, detailDTO.getAppAmount());
                    break;
                default:
                    break;
            }
            usedDeviceAmountDTO.setInUsedAmount(count);
        }
        return usedDeviceAmountDTO;
    }

    private void setUnUsedAmount(UsedDeviceAmountDTO usedDeviceAmountDTO, Integer count, Integer amount) {
        if(-1 == amount){
            usedDeviceAmountDTO.setUnUsedAmount(-1);
        }else {
            usedDeviceAmountDTO.setUnUsedAmount(amount == 0 ? 0 : amount - count);
        }
    }
}
