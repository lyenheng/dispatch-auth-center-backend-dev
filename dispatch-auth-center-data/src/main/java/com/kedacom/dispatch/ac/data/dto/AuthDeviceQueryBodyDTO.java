package com.kedacom.dispatch.ac.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 设备权限服务设备查询参数
 * @Auther: liuyanhui
 * @Date: 2022/9/5 13:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDeviceQueryBodyDTO {

    private Integer pageNo;
    private Integer pageSize;
    // 是否按角色有效期查询(roleIds,newValidDate)，true:查询给定 角色-有效期 下设备的并集，false:查询非 角色-有效期 下设备的并集
    private Boolean withRole = true;
    // 有效期
     private String validDate;

    private List<String> deviceIds;

    /**
     * 当 withRole = true 时，此字段必传
     * 0 查询全部设备（不包括未授权设备）
     * 1 查询未过期设备
     * 2 查询已过期设备
     * 3 查询指定有效期设备
     */
    private Integer deviceQueryType;

    private List<String> deviceGroupIds;
    private List<String> deviceTypes;


    private Boolean excludeRole;
    // 排除role之间的关系，取值范围[or,and]，默认and
    // 情形1： withRole=false,excludeRole=true,multiExcludeRoleModel='or'：查询多个角色未授权设备的并集
    private String multiExcludeRoleModel;

    private List<String> roleIds;

}
