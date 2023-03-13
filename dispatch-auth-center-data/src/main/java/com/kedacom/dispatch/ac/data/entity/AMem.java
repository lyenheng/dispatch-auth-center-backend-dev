package com.kedacom.dispatch.ac.data.entity;

import com.kedacom.dispatch.ac.data.vo.AMemVO;
import com.kedacom.kidp.base.data.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * @author : liuyue
 * @Description : 根据APIKey授权设备实体表
 * @date : 2021/11/8 16:05
 */
@Entity
@Data
@Table(name = "tmp_a_mem")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class AMem extends BaseEntity{

    /**
     * 设备id
     */
    @Basic
    private String memId;

    /**
     * 设备类型
     */
    @Basic
    private String memTy;

    /**
     * APIKey
     */
    @Basic
    private String apiId;


    public AMem(AMemVO aMemVO){
        this.apiId = aMemVO.getApiId();
        this.memId = aMemVO.getMemId();
        this.memTy = aMemVO.getMemTy();
    }
}
