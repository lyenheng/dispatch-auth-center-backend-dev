package com.kedacom.dispatch.ac.data.entity;

import com.kedacom.dispatch.ac.data.vo.RMemVO;
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
 * @Description : 根据用户角色授权设备实体表
 * @date : 2021/11/8 16:05
 */
@Entity
@Data
@Table(name = "tmp_r_mem")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class RMem extends BaseEntity{

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
     * 角色id
     */
    @Basic
    private String roleId;


    public RMem(RMemVO rmemVO){
        this.roleId = rmemVO.getRoleId();
        this.memId = rmemVO.getMemId();
        this.memTy = rmemVO.getMemTy();

    }
}
