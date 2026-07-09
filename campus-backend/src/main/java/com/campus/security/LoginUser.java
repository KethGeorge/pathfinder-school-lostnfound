package com.campus.security;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 当前登录用户信息，从 Token 解析后放入请求上下文
 */
@Data
@AllArgsConstructor
public class LoginUser {
    private Long userId;
    private String role;

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
