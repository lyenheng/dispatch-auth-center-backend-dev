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
@Table(name = "avcs_device_type")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class AvcsDeviceType extends BaseEntity {
    private String deviceType;
    private String deviceTypeName;
    // 0:监控,1:会议终端,2:电话,3:数字集群,4:APP用户,5:其他
    private String category;
    // 设备类型是否可编辑  0:是  1:否
    private String isEdit;
}
