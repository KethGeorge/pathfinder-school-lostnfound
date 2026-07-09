package com.campus.security;

import java.lang.annotation.*;

/**
 * 标注在 Controller 方法或类上：需要登录才能访问。
 * admin=true 表示需要管理员角色。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {
    boolean admin() default false;
}
