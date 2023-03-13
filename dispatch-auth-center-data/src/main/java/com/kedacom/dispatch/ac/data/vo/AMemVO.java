package com.kedacom.dispatch.ac.data.vo;

import com.kedacom.dispatch.ac.data.entity.AMem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author : liuyue
 * @Description :
 * @date : 2021/11/8 16:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AMemVO extends MemVO {

    /**
     * APIKey
     */
    @NotNull(message = "APIKey不能为空！")
    private String apiId;

    public AMemVO(AMem aMem) {
        this.apiId = aMem.getApiId();
        setMemId(aMem.getMemId());
        setMemTy(aMem.getMemTy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AMemVO aMemVO = (AMemVO) o;

        return apiId.equalsIgnoreCase(aMemVO.apiId) &&
                getMemId().equalsIgnoreCase(aMemVO.getMemId()) &&
                getMemTy().equalsIgnoreCase(aMemVO.getMemTy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiId, getMemId(), getMemTy());
    }
}