package com.kedacom.dispatch.ac.data.dto;

import lombok.Data;

import java.util.List;

/**
 * @author chenyang
 * @date 2021/12/2 9:37
 */
@Data
public class QueryNumByGroupDTO {
    private List<String> groupIds;
    private String deviceType;
}
