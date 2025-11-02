package com.vinn.travelmanager.common;

/**
 * 响应状态码枚举
 */
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    
    // 用户相关错误码
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USERNAME_OR_PASSWORD_ERROR(1003, "用户名或密码错误"),
    PASSWORD_ERROR(1004, "密码错误"),
    EMAIL_ALREADY_EXISTS(1005, "邮箱已被注册"),
    PHONE_ALREADY_EXISTS(1006, "手机号已被注册"),
    
    // 参数验证错误
    PARAM_ERROR(2001, "参数错误"),
    PARAM_MISSING(2002, "参数缺失"),
    
    // 系统错误
    SYSTEM_ERROR(5000, "系统错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

