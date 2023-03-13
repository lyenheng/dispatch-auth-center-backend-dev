package com.kedacom.dispatch.ac.data.dto;

import com.kedacom.dispatch.ac.data.vo.AMemVO;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/04/22/ 16:01
 */
@Data
public class AmenListDTO {
    List<AMemVO> aMemVOList;
    String valiDate;
}
