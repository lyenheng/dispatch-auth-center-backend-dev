package com.kedacom.dispatch.ac.data.dao;

import com.kedacom.dispatch.ac.data.entity.AvcsActiveCoding;
import com.kedacom.dispatch.ac.data.entity.RMem;
import com.kedacom.kidp.base.data.common.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/4/19 18:22
 */

@Repository
public interface AvcsActiveCodingDao extends BaseRepository<AvcsActiveCoding> {

    AvcsActiveCoding findByState(Integer state);
}
