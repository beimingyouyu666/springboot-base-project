package com.yangk.baseproject.common.exception;

import com.yangk.baseproject.common.enums.EnumCommomSysErrorCode;
import lombok.Data;

/**
 * @Description 业务异常
 * @Author yangkun
 * @Date 2020/4/9
 * @Version 1.0
 * @blame yangkun
 */
@Data
public class BusinessRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 5030098465020190726L;
    private String errcode;

    public BusinessRuntimeException(String message, String errcode) {
        super(message);
        this.errcode = errcode;
    }

    public BusinessRuntimeException(String message) {
        super(message);
    }

    public static BusinessRuntimeException buildBusinessRuntimeException(EnumCommomSysErrorCode sysErrorCode, String desc) {
        return new BusinessRuntimeException(desc, sysErrorCode.getValue());
    }

    public static BusinessRuntimeException buildBusinessRuntimeException(String desc) {
        return new BusinessRuntimeException(desc);
    }
}
