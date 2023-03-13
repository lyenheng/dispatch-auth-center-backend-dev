package com.kedacom.dispatch.ac.data.enums;

import lombok.Getter;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/6/7 16:33
 */
public enum DeviceCategoryEnum {

    IPC("监控", "0"),
    TERMINAL("会议终端", "1"),
    TELEPHONE("电话", "2"),
    PTT("数字集群", "3"),
    APP("APP用户", "4"),
    OTHER("其他", "5");
    @Getter
    private String desc;

    @Getter
    private String category;

    DeviceCategoryEnum(String desc, String category) {
        this.desc = desc;
        this.category = category;
    }
}
