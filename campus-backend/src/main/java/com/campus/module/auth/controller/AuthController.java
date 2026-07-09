package com.campus.module.auth.controller;

import com.campus.common.Result;
import com.campus.module.auth.dto.LoginDTO;
import com.campus.module.auth.dto.LoginVO;
import com.campus.module.auth.dto.RegisterDTO;
import com.campus.module.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口：注册、登录
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success("注册成功", null);
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = authService.login(dto);
        return Result.success(vo);
    }
}
