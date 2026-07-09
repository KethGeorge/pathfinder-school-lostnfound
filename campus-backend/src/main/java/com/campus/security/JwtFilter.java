package com.campus.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 过滤器：若请求带有效 Token，则解析并放入 LoginUserHolder。
 * 不在此处拦截；是否需要登录由 AuthInterceptor 判断。
 */
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Long userId = jwtUtil.getUserId(token);
                String role = jwtUtil.getRole(token);
                LoginUserHolder.set(new LoginUser(userId, role));
            } catch (JwtException | IllegalArgumentException e) {
                // Token 无效/过期：不设置上下文，交给拦截器处理需登录接口
                log.debug("无效Token: {}", e.getMessage());
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            LoginUserHolder.clear();
        }
    }
}
