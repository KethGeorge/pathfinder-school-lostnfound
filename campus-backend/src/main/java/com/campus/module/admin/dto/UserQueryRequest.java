package com.campus.module.admin.dto;

import com.campus.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageRequest {

    private String keyword; // 搜索学号/用户名/手机/邮箱
    private String role;
    private Integer isBanned;
}
