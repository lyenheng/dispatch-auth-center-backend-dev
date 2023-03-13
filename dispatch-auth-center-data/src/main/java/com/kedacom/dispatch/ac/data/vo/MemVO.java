package com.kedacom.dispatch.ac.data.vo;

import com.kedacom.dispatch.ac.data.entity.AMem;
import com.kedacom.dispatch.ac.data.entity.RMem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author : liuyue
 * @Description :
 * @date : 2021/11/9 15:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemVO implements Serializable {
    /**
     * 设备id
     */
    @NotBlank(message = "设备id不能为空！")
    private String memId;

    /**
     * 设备类型
     */
    @NotBlank(message = "设备类型不能为空！")
    private String memTy;


    public MemVO(RMem rMem){
        this.memId = rMem.getMemId();
        this.memTy = rMem.getMemTy();
    }

    public MemVO(AMem aMem) {
        this.memId = aMem.getMemId();
        this.memTy = aMem.getMemTy();
    }
}
