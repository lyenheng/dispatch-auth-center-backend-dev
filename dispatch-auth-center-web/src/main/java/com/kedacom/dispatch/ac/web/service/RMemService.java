package com.kedacom.dispatch.ac.web.service;

import com.kedacom.dispatch.ac.data.vo.MemVO;
import com.kedacom.dispatch.ac.data.vo.RMemVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : liuyue
 * @Description :
 * @date : 2021/11/8 17:25
 */
public interface RMemService {

    List<RMemVO> addRMem(List<RMemVO> rMemVOList);

    Map<String, List<MemVO>> searchMemByR(List<String> roles);

    void deleteByR(List<String> rIds);

    List<RMemVO> removeDupAuth(List<RMemVO> rMemVOList);
}
