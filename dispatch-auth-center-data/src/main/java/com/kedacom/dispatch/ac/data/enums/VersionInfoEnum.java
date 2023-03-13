package com.kedacom.dispatch.ac.data.enums;

import lombok.Getter;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/04/15/ 17:13
 */
public enum VersionInfoEnum {
    LITE("一体机版本", " 0"),
    STANDARD("融合通信版本(标准版)", "1"),
    POLI_STANDARD("政法标准版", "2"),
    POLI_BASIC("政法基础版", "3");
    @Getter
    private String desc;
    @Getter
    private String value;

    VersionInfoEnum(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }
}
