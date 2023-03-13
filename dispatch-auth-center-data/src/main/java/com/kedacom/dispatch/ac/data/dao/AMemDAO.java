package com.kedacom.dispatch.ac.data.dao;

import com.kedacom.dispatch.ac.data.entity.AMem;
import com.kedacom.kidp.base.data.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : liuyue
 * @Description :
 * @date : 2021/11/8 17:03
 */
@Repository
public interface AMemDAO extends BaseRepository<AMem> {

    void deleteByApiIdIn(List<String> apiKey);

    List<AMem> findAllByApiId(String apiKey);

}
