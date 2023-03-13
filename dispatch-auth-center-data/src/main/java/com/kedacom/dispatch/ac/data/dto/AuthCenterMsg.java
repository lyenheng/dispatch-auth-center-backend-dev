package com.kedacom.dispatch.ac.data.dto;

import com.kedacom.dispatch.common.data.exception.CodeMessage;
import lombok.Data;

@Data
public class AuthCenterMsg implements CodeMessage {

    private String code;

    private String message;

    public AuthCenterMsg(String code, String content) {
        this.code = code;
        this.message = content;
    }
}