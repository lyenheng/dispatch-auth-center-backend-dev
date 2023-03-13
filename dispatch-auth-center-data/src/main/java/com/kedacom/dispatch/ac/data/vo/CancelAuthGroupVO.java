package com.kedacom.dispatch.ac.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 按分组取消授权vo
 * @Auther: liuyanhui
 * @Date: 2022/5/12 14:33
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelAuthGroupVO {
    private List<String> groupIds;
    private List<String> roleIds;
    private List<String> deviceTypes;
    private String validDate;
}
