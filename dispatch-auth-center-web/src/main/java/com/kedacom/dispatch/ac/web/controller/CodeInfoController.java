package com.kedacom.dispatch.ac.web.controller;

import com.alibaba.fastjson.JSON;
import com.kedacom.avcs.dispatch.manage.client.service.ManageClient;
import com.kedacom.avcs.dispatch.manage.data.dto.LicenseDTO;
import com.kedacom.avcs.dispatch.secretkey.data.dto.ActivationDTO;
import com.kedacom.avcs.dispatch.secretkey.web.service.SecretKeyService;
import com.kedacom.ctsp.authz.oauth2.client.service.OAuth2ResourceService;
import com.kedacom.ctsp.authz.oauth2.client.service.OAuth2UtilService;
import com.kedacom.ctsp.authz.oauth2.core.vo.ClientQueryParam;
import com.kedacom.ctsp.authz.vo.AuthUserVO;
import com.kedacom.ctsp.orm.param.Term;
import com.kedacom.ctsp.orm.param.TermEnum;
import com.kedacom.ctsp.web.controller.message.ResponseMessage;
import com.kedacom.ctsp.web.entity.page.PagerResult;
import com.kedacom.dispatch.ac.web.service.CodeInfoService;
import com.kedacom.dispatch.common.data.dto.rbac.PersonQueryDTO;
import com.kedacom.dispatch.common.data.dto.rbac.SimplePersonDTO;
import com.kedacom.dispatch.common.data.dto.rbac.SimpleRoleDTO;
import com.kedacom.dispatch.common.rbac.util.RbacConvertUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scala.annotation.meta.param;

import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chenyang
 * @date 2022/3/9 13:40
 */
@RestController
@RequestMapping("/code")
@Slf4j
public class CodeInfoController {

    @Autowired
    private CodeInfoService codeInfoService;
    @Autowired
    private SecretKeyService secretKeyService;
    @Autowired
    private ManageClient manageClient;
    @Autowired
    private OAuth2UtilService oAuth2UtilService;
    @Autowired
    private OAuth2ResourceService oAuth2ResourceService;
//    @ApiOperation("获取激活码时效容量模块信息")
//    @GetMapping("/info")
//    public ResponseMessage<Map<String, DetailDTO>> getCodeInfo() {
//        return ResponseMessage.ok(secretKeyService.getCapacityPackage());
//    }
    @GetMapping("/encrypt")
    @ApiOperation("生成项目标识")
    public ResponseMessage<String> encryptKey() {
        return ResponseMessage.ok(secretKeyService.encryptKey());
    }

    @ApiOperation("获取购买的并发量")
    @PostMapping("/getConcurrentAmount")
    public ResponseMessage<Integer> getConcurrentAmount() {
        return ResponseMessage.ok(codeInfoService.getConcurrentAmount());
    }

    @ApiOperation("获取版本")
    @GetMapping("/getVerionInfo")
    public ResponseMessage<Integer> getVerionInfo() {
        return ResponseMessage.ok(secretKeyService.getVersionInfo());
    }

    @PostMapping("/jXActivation")
    @ApiOperation("解析激活码")
    public ResponseMessage<LicenseDTO> jXActivation(@RequestBody ActivationDTO activationDTO) {
        LicenseDTO licenseDTO = null;
        try {
            licenseDTO = manageClient.decrypt(activationDTO.getNewActiveCoding());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseMessage.ok(licenseDTO);
    }

    @PostMapping("/jXIncrement")
    @ApiOperation("解析增量容量")
    public ResponseMessage<LicenseDTO> jXIncrement(@RequestBody ActivationDTO activationDTO) {
        LicenseDTO incrementLicenseDto = null;
        try {
            String increment = new String(Base64.getDecoder().decode(activationDTO.getNewActiveCoding().getBytes()));
            incrementLicenseDto = JSON.parseObject(increment, LicenseDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseMessage.ok(incrementLicenseDto);
    }

    /**
     * 根据项目标识秘钥查询人员角色
     */
    @ApiOperation(value = "根据项目标识秘钥查看人员角色",httpMethod = "POST", response = ResponseMessage.class)
    @PostMapping ("/queryPersonRolesByClient")
    public ResponseMessage<List<SimplePersonDTO> > queryPersonByRoles (@RequestBody PersonQueryDTO query){
        ClientQueryParam clientQueryParam = new ClientQueryParam();
        clientQueryParam.setClientId("dispatch-micoservice");
        clientQueryParam.setClientSecret("vKwAAC");
        Term term = new Term();
        term.and("id", TermEnum.in, query.getPersonIds());
        clientQueryParam.setPaging(query.getPaging());
        clientQueryParam.setPageNo(query.getPageNo());
        clientQueryParam.setPageSize(query.getPageSize());
        clientQueryParam.and(term);
        ResponseMessage<PagerResult<AuthUserVO>> pageResp = oAuth2ResourceService.queryUser(clientQueryParam);
        List<SimplePersonDTO> users = pageResp.getResult().convertTo(RbacConvertUtil::cvtAuthUserVO2SimplePerson).getData()
                .stream().distinct().collect(Collectors.toList());
        if (query.getOnlyCurrentPrjRole()) {
            for (SimplePersonDTO user : users) {
                Set<SimpleRoleDTO> roleSet = user.getRoles();
                roleSet.removeIf(next -> !clientQueryParam.getClientId().equals(next.getClientSign()));
            }
        }
        return ResponseMessage.ok(users);
    }
//    @ApiOperation("获取TAB")
//    @GetMapping("/getTabs")
//    public ResponseMessage<List<String>> getTabs() {
//        return ResponseMessage.ok(codeInfoService.getTabs());
//    }
}
