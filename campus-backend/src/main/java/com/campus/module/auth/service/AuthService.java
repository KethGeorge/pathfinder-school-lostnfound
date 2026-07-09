package com.campus.module.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.exception.BusinessException;
import com.campus.module.auth.dto.LoginDTO;
import com.campus.module.auth.dto.LoginVO;
import com.campus.module.auth.dto.RegisterDTO;
import com.campus.module.user.entity.User;
import com.campus.module.user.mapper.UserMapper;
import com.campus.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务：注册、登录
 */
@Service
public class AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    /** 注册 */
    public void register(RegisterDTO dto) {
        // 检查学号是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStudentId, dto.getStudentId());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("学号已被注册");
        }

        User user = new User();
        user.setStudentId(dto.getStudentId());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("USER");
        user.setIsBanned(0);
        user.setIsDeleted(0);
        userMapper.insert(user);
    }

    /** 登录 */
    public LoginVO login(LoginDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStudentId, dto.getStudentId());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("学号或密码错误");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("学号或密码错误");
        }
        if (user.getIsBanned() == 1) {
            throw new BusinessException("账号已被封禁");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        return new LoginVO(token, user.getId(), user.getStudentId(), user.getUsername(), user.getRole());
    }
}
