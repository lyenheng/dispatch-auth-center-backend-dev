package com.kedacom.dispatch.ac.data.exception;

import com.kedacom.dispatch.common.data.exception.CodeMessage;

/**
 * @author : liuyue
 * @Description :
 * @date : 2021/11/9 13:21
 */
public enum AuthExceptionCode implements CodeMessage {

    AT100("AT100", "设备id不能为空！"),
    AT101("AT101", "设备类型不能为空！"),
    AT102("AT102", "aId不能为空！"),
    AT103("AT103", "用户角色不能为空！"),
    AT104("AT104", "设备类型不正确！"),
    AT105("AT105", "{deviceType}临时设备容量不够，无法进行授权！"),
    AT106("AT106", "参数为空！"),
    AT107("AT107", "授权期限不能为空！"),
    AT108("AT108", "所选设备授权已存在"),

    AT200("AT200", "请至少选择一个角色！"),
    AT201("AT201", "请至少勾选一个设备！"),
    AT202("AT202", "{deviceType}超出剩余容量{num}个"),
    AT203("AT203", "设备类型不能为空"),
    AT204("AT204", "请至少选择一个分组进行授权"),
    AT205("AT205", "存在尚未完成的授权操作，请耐心等待"),
    AT206("AT206", "未知异常，操作终止"),
    AT207("AT207", "设备类型不能为空"),
    AT208("AT208", "当前已选分组下没有{deviceType}类型的设备"),
    AT209("AT209", "授权日期不能为空"),

    AT300("AT300", "查询已使用并发量失败"),
    AT301("AT301", "授权信息查询失败"),
    AT302("AT302", "设备已授权！"),
    AT303("AT303", "无效的授权日期！"),
    AT304("AT304", "所选分组下无设备！"),
    AT305("AT305", "当前有效期下无设备！"),
    AT306("AT306", "所选设备类型下无设备！"),
    AT307("AT300", "角色查询失败"),
    AT308("AT300", "设备查询失败"),
    AT309("AT309", "设备不存在"),
    AT310("AT310", "过期设备删除失败"),
    AT311("AT311", "设备类型查询失败"),
    AT312("AT312", "无效的设备类型！"),
    AT313("AT313", "{deviceType}下存在已授权的设备，无法移除"),
    AT314("AT314", "{deviceType}类型不可编辑"),
    AT315("AT315", "设备权限：部分设备查询超时"),


    AT401("AT401", "开发平台链接超时"),
    AT402("AT402", "设备权限连接超时"),
    AT403("AT403", "融合通信连接超时"),
    AT404("AT404", "设备权限连接失败"),
    ;


    private String code;

    private String desc;

    AuthExceptionCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return desc;
    }
}
