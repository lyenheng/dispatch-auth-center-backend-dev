package com.kedacom.dispatch.ac.data.entity;

import com.kedacom.kidp.base.data.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/04/19/ 16:18
 */
@Entity
@Data
@Table(name = "avcs_active_coding")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class AvcsActiveCoding extends BaseEntity {
    private String projectName;
    private String projectSecretKey;
    private String activeCode;
    private Integer versionInfo;
    private Integer state;
    private String increment;
}
