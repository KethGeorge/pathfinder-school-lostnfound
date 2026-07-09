package com.campus.module.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录成功返回：Token + 用户基本信息
 */
@Data
@AllArgsConstructor
public class LoginVO {
    private String token;
    private Long userId;
    private String studentId;
    private String username;
    private String role;
}
