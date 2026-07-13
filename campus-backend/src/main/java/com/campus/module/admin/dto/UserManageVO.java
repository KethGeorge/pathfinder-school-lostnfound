package com.campus.module.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户管理 VO
 */
@Data
public class UserManageVO {

    private Long id;
    private String studentId;
    private String username;
    private String phone;
    private String email;
    private String role;
    private Integer isBanned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long lostFoundCount; // 该用户发布的失物招领数量
}
