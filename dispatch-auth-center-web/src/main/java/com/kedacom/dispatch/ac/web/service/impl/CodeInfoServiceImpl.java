package com.kedacom.dispatch.ac.web.service.impl;

import com.kedacom.dispatch.ac.web.service.CodeInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author chenyang
 * @date 2022/3/9 14:02
 */
@Service
@Slf4j
public class CodeInfoServiceImpl implements CodeInfoService {

//    @Autowired
//    private ActiveCodingClient activeCodingClient;
//    @Autowired
//    private AvcsActiveCodingDao avcsActiveCodingDao;



    /**
     * 获取并发量 TODO
     * @return
     */
    @Override
    public Integer getConcurrentAmount() {
//        Integer versionInfo = secretKeyService.getVersionInfo();
//        if (versionInfo == 0) {
//            return ImacVersionEnum.getByValue().getConcurrentAmount();
//        } else if (versionInfo == 1) {
//            return -1;
//        } else {
//            return 0;
//        }

        return -1;
    }


//    private CodeInfoDTO setData(CapacityPackageDTO capacityPackage, Date expirationTime, Integer versionInfo, Integer currentDispatchingDeviceCount) {
//        if (null != versionInfo && versionInfo == 1) {
//            CodeInfoDTO codeInfoDTO = new CodeInfoDTO();
//            BeanUtils.copyProperties(capacityPackage, codeInfoDTO);
//            codeInfoDTO.setIsUsedConcurrentAmount(currentDispatchingDeviceCount);
//            codeInfoDTO.setExpirationTime(expirationTime);
//            codeInfoDTO.setVersionInfo(versionInfo);
//            return codeInfoDTO;
//        } else if (null != versionInfo && versionInfo == 0) {
//            CodeInfoDTO codeInfoDTO = new CodeInfoDTO();
//            ImacVersionEnum byValue = ImacVersionEnum.getByValue();
//            CapacityPackageDTO capacityPackageDTO = new CapacityPackageDTO();
//            capacityPackageDTO.setValue(byValue.getValue());
//            capacityPackageDTO.setDesc(byValue.getDesc());
//            capacityPackageDTO.setIpcAmount(byValue.getIpcAmount());
//            capacityPackageDTO.setTerminalAmount(byValue.getTerminalAmount());
//            capacityPackageDTO.setTelephoneAmount(byValue.getTelephoneAmount());
//            capacityPackageDTO.setPttAmount(byValue.getPttAmount());
//            capacityPackageDTO.setAppAmount(byValue.getAppAmount());
//            capacityPackageDTO.setOtherAmount(byValue.getOtherAmount());
//            capacityPackageDTO.setConcurrentAmount(byValue.getConcurrentAmount());
//            BeanUtils.copyProperties(capacityPackageDTO, codeInfoDTO);
//            codeInfoDTO.setIsUsedConcurrentAmount(currentDispatchingDeviceCount);
//            codeInfoDTO.setExpirationTime(expirationTime);
//            codeInfoDTO.setVersionInfo(versionInfo);
//            return codeInfoDTO;
//        } else {
//            return null;
//        }
//    }


}
