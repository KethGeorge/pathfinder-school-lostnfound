package com.campus.common;

import lombok.Data;

/**
 * 统一响应封装
 * { "code": 200, "message": "success", "data": {...} }
 */
@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /** 成功，带数据 */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /** 成功，带消息和数据 */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /** 成功，无数据 */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /** 失败，指定错误码和消息 */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /** 失败，默认 400 */
    public static <T> Result<T> error(String message) {
        return new Result<>(400, message, null);
    }
}
