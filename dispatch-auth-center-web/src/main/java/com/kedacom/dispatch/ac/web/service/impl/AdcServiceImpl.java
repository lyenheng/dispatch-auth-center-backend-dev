package com.kedacom.dispatch.ac.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kedacom.adc.common.dto.device.RaDeviceDTO;
import com.kedacom.adc.common.dto.device.ValidDateItemDTO;
import com.kedacom.ctsp.web.entity.page.PagerResult;
import com.kedacom.dispatch.ac.data.constants.AuthDeviceQueryBodyConstant;
import com.kedacom.dispatch.ac.data.constants.QueryParamKeysConstant;
import com.kedacom.dispatch.ac.data.dto.AuthDeviceQueryBodyDTO;
import com.kedacom.dispatch.ac.data.dto.AuthDeviceQueryUrlParamDTO;
import com.kedacom.dispatch.ac.web.service.AdcService;
import com.kedacom.dispatch.common.data.dto.adc.SimpleAdcDeviceDTO;
import com.kedacom.dispatch.common.resource.auth.feign.req.HeaderReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * @Description: 授权权限服务，动态条件查询
 * @Auther: liuyanhui
 * @Date: 2022/08/11/ 13:49
 */
@Service
@Slf4j
public class AdcServiceImpl implements AdcService {
    // 设备权限：设备查询地址
    @Value("${kedacom.openapi.adc.serverUri: http://192.168.9.208:9094/ra}")
    private String adcUrl;

    // 查询未授权设备
    @Override
    public PagerResult<SimpleAdcDeviceDTO> queryDevices(String url, RestTemplate restTemplate, HeaderReq headerReq,
                                                        AuthDeviceQueryBodyDTO authDeviceQueryBodyDTO,Boolean isFilterRoles,String newValidDate) {
        if(StringUtils.isNotEmpty(authDeviceQueryBodyDTO.getValidDate())){
            String valiDate = authDeviceQueryBodyDTO.getValidDate().replaceAll("[-+,:+,\\s]", "");
            authDeviceQueryBodyDTO.setValidDate(valiDate);
        }
        if(StringUtils.isNotEmpty(newValidDate)){
            newValidDate =newValidDate.replaceAll("[-+,:+,\\s]", "");
        }
        HttpHeaders requestHeaders = new HttpHeaders();
        if(Boolean.TRUE.equals(authDeviceQueryBodyDTO.getExcludeRole())
                && AuthDeviceQueryBodyConstant.MULTI_EXCLUDE_ROLE_MODEL_OR.equals(authDeviceQueryBodyDTO.getMultiExcludeRoleModel())) {
            // 查询未授权设备，角色在body中传参，header中角色置空
            requestHeaders.add("roleIds", authDeviceQueryBodyDTO.getRoleIds().get(0));
        }else{
            // 根据角色有效期查询设备
            requestHeaders.add("roleIds", headerReq.getRoleIds());
        }
        log.info("RestTemplate Request ---> POST URL: " + url);
        log.info("RestTemplate Request ---> POST HEADER roleIds = " + requestHeaders.get("roleIds"));
        requestHeaders.add("content-type", MediaType.APPLICATION_JSON_UTF8.toString());
        String params = JSON.toJSONString(authDeviceQueryBodyDTO);
        log.info("RestTemplate Request ---> POST RequestBody: " + params);
        HttpEntity<String> requestEntity = new HttpEntity<>(params,requestHeaders);
        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Object.class);
        HttpStatus statusCode = response.getStatusCode();
        if (HttpStatus.OK == statusCode) {
            Object body = response.getBody();
            if (null != body) {
                LinkedHashMap<String, Object> res = (LinkedHashMap)body;
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                PagerResult<Object> result = objectMapper.convertValue(res.get("result"),PagerResult.class );
                List<Object> data = result.getData();
                if(CollectionUtils.isEmpty(data)){
                    return null;
                }
                PagerResult<SimpleAdcDeviceDTO> pagerResult = new PagerResult<>();
                BeanUtils.copyProperties(result,pagerResult);
                List<SimpleAdcDeviceDTO> list = new ArrayList<>();
                pagerResult.setData(list);
                for (Object datum : data) {
                    SimpleAdcDeviceDTO raDeviceDTO = objectMapper.convertValue(datum, SimpleAdcDeviceDTO.class);
                    // raDeviceDTO.setDataSource(null);
                    List<ValidDateItemDTO> validDateItems = raDeviceDTO.getValidDateItems();
                    if(!CollectionUtils.isEmpty(validDateItems)){
                        for (ValidDateItemDTO validDateItem : validDateItems) {
                            String validDate = validDateItem.getValidDate();
                            if(null != validDate && validDate.equals(newValidDate)){
                                raDeviceDTO.setHasValidDate(true);
                                break;
                            }
                        }
                    }else if(!CollectionUtils.isEmpty(raDeviceDTO.getRoleIds())){
                        for (String roleId : raDeviceDTO.getRoleIds()) {
                            if(roleId.contains("_")){
                                String validDate = roleId.split("_")[1];
                                if(null != validDate && validDate.equals(newValidDate)){
                                    raDeviceDTO.setHasValidDate(true);
                                    break;
                                }
                            }
                        }
                    }
                    if(Boolean.TRUE.equals(isFilterRoles)){
                        raDeviceDTO.setRoleIds(null);
                        raDeviceDTO.setValidDateItems(null);
                    }
                    list.add(raDeviceDTO);
                }
                if(null != pagerResult && !CollectionUtils.isEmpty(pagerResult.getData())){
                    log.info("============ 共 " + pagerResult.getTotalPages() + " 页 ，"
                            + " 每页数量 " + pagerResult.getPageSize() + " ，"
                            + " 查询到第 " + (pagerResult.getPageNo() + 1) + " 页 ，"
                            + " 返回数量 :" + pagerResult.getData().size() );
                }
                log.info("============ 设备权限查询耗时 :" + res.get("timeElapsed") + "ms");
                return pagerResult;
            }
        }
        return null;
    }
    /**
     * 构建设备权限动态查询路径
     * @return String url
     */
    @Override
    public String createUrl() {
        StringBuffer url = new StringBuffer();
        url.append(adcUrl + "/device");
        // 过滤掉没有状态的设备
        url.append("?f_notnull_status=true");
        return url.toString();
    }

//    public StringBuilder getRootUrlBuilder(AuthDeviceQueryUrlParamDTO authDeviceQueryUrlParamDTO) {
//        StringBuilder url = new StringBuilder();
//        url.append(adcUrl);
//        url.append("/device");
//        url.append("?" + QueryParamKeysConstant.PAGE_NO + "=" + authDeviceQueryUrlParamDTO.getPageNo());
//        url.append("&" + QueryParamKeysConstant.PAGE_SIZE + "=" + authDeviceQueryUrlParamDTO.getPageSize());
//
//        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(authDeviceQueryUrlParamDTO.getDeviceTypes())) {
//            String types = Strings.join(authDeviceQueryUrlParamDTO.getDeviceTypes(), ",");
//            url.append("&" + QueryParamKeysConstant.F_IN_DEVICETYPE + "=" + types);
//        }
//        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(authDeviceQueryUrlParamDTO.getGroupIds())) {
//            String groups = Strings.join(authDeviceQueryUrlParamDTO.getGroupIds(), ",");
//            url.append("&" + QueryParamKeysConstant.F_IN_GROUPID + "=" + groups);
//        }
//        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(authDeviceQueryUrlParamDTO.getDevices())) {
//            String ids = authDeviceQueryUrlParamDTO.getDevices()
//                    .stream()
//                    .map(DeviceDTO::getId)
//                    .reduce((d1, d2) -> d1 + "," + d2)
//                    .get();
//            url.append("&" + QueryParamKeysConstant.F_IN_ID + "=" + ids);
//        }
//        if (StringUtils.isNotEmpty(authDeviceQueryUrlParamDTO.getDeviceName())) {
//            url.append("&" + QueryParamKeysConstant.DEVICE_NAME + "=" + authDeviceQueryUrlParamDTO.getDeviceName());
//        }
//        if (StringUtils.isNotEmpty(authDeviceQueryUrlParamDTO.getGbid())) {
//            url.append("&" + QueryParamKeysConstant.F_IN_GBID + "=" + authDeviceQueryUrlParamDTO.getGbid());
//        }
//        return url;
//    }


}
