package com.kedacom.dispatch.ac.data.dao;

import com.kedacom.dispatch.ac.data.entity.RMem;
import com.kedacom.kidp.base.data.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : liuyue
 * @Description :
 * @date : 2021/11/8 17:15
 */
@Repository
public interface RMemDAO extends BaseRepository<RMem> {

    List<RMem> findAllByRoleId(String rId);

    void deleteByRoleIdIn(List<String> rIds);


}
