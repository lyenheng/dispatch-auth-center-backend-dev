package com.kedacom.dispatch.ac.data.dto;

import lombok.Data;

/**
 * @author chenyang
 * @date 2021/11/25 15:29
 */
@Data
public class ApiKeyInfoDTO {
    /**
     * apikey
     */
    private String apiKey;
    /**
     *应用开发公司
     */
    private String appCompany;
    /**
     *应用简介
     */
    private String appDescription;
    /**
     *项目负责人
     */
    private String appLeader;
    /**
     *应用名称
     */
    private String appName;
    /**
     *建设单位
     */
    private String contractorUnit;
    /**
     *联系方式
     */
    private String leaderContact;
    /**
     *用户昵称
     */
    private String name;
    /**
     *用户id
     */
    private String userId;
    /**
     * 用户名
     */
    private String userName;
}
