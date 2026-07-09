package com.campus.security;

import com.campus.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * 鉴权拦截器：检查方法/类上的 @RequireAuth 注解。
 * - 需登录但无上下文 → 401
 * - 需管理员但非 ADMIN → 403
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        Method method = handlerMethod.getMethod();
        RequireAuth requireAuth = method.getAnnotation(RequireAuth.class);
        if (requireAuth == null) {
            requireAuth = handlerMethod.getBeanType().getAnnotation(RequireAuth.class);
        }
        if (requireAuth == null) {
            return true; // 无注解，公开接口
        }

        LoginUser user = LoginUserHolder.get();
        if (user == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        if (requireAuth.admin() && !user.isAdmin()) {
            throw new BusinessException(403, "无管理员权限");
        }
        return true;
    }
}
