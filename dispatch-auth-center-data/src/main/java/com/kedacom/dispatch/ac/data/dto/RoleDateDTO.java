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
public class RoleDateDTO {

    private List<String> roleIds;
    private String date;
}
