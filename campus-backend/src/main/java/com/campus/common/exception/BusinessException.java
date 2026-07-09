package com.campus.common.exception;

import lombok.Getter;

/**
 * 业务异常：Service 层校验不通过时抛出，由 GlobalExceptionHandler 统一捕获
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
