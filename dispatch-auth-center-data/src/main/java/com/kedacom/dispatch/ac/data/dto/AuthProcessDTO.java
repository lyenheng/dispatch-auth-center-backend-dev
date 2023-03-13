package com.kedacom.dispatch.ac.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 授权进度
 * @Auther: liuyanhui
 * @Date: 2022/9/7 16:34
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthProcessDTO {
    // 授权进度
    private String progress;
    //  1：授权   2：变更授权  3：取消授权
    private String type;
}
