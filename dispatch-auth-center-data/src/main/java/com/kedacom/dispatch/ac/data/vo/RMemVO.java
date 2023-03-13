package com.kedacom.dispatch.ac.data.vo;

import com.kedacom.dispatch.ac.data.entity.RMem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Objects;

/**
 * @author : liuyue
 * @Description :
 * @date : 2021/11/8 16:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RMemVO extends MemVO{

    /**
     * 用户角色id
     */
    @NotBlank(message = "用户角色不能为空！")
    private String roleId;

    public RMemVO(RMem rMem) {
        this.roleId = rMem.getRoleId();
        setMemId(rMem.getMemId());
        setMemTy(rMem.getMemTy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RMemVO rMemVO = (RMemVO) o;

        return roleId.equalsIgnoreCase(rMemVO.roleId) &&
                getMemId().equalsIgnoreCase(rMemVO.getMemId()) &&
                getMemTy().equalsIgnoreCase(rMemVO.getMemTy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, getMemId(), getMemTy());
    }
}