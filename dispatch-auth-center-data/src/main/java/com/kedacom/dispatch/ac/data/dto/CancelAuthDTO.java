package com.kedacom.dispatch.ac.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chenyang
 * @date 2021/11/17 19:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelAuthDTO {
    private List<String> roleIds;
    private List<String> deviceTypes;
    private String vlidDate;
}
