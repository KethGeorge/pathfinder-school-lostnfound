package com.campus.module.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.common.Result;
import com.campus.module.admin.dto.*;
import com.campus.module.admin.service.AdminService;
import com.campus.security.RequireAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台控制器（所有接口需要管理员权限）
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@RequireAuth(admin = true)
public class AdminController {

    private final AdminService adminService;

    /**
     * 获取数据看板
     */
    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        DashboardVO dashboard = adminService.getDashboard();
        return Result.success(dashboard);
    }

    /**
     * 查询用户列表
     */
    @GetMapping("/users")
    public Result<IPage<UserManageVO>> queryUsers(UserQueryRequest request) {
        IPage<UserManageVO> page = adminService.queryUsers(request);
        return Result.success(page);
    }

    /**
     * 封禁用户
     */
    @PostMapping("/users/{userId}/ban")
    public Result<Void> banUser(@PathVariable Long userId) {
        adminService.banUser(userId, 1);
        return Result.success();
    }

    /**
     * 解封用户
     */
    @PostMapping("/users/{userId}/unban")
    public Result<Void> unbanUser(@PathVariable Long userId) {
        adminService.banUser(userId, 0);
        return Result.success();
    }

    /**
     * 强制删除失物招领
     */
    @DeleteMapping("/lostfound/{id}")
    public Result<Void> deleteLostFound(@PathVariable Long id) {
        adminService.deleteLostFound(id);
        return Result.success();
    }

    /**
     * 修改失物招领状态
     */
    @PutMapping("/lostfound/{id}/status")
    public Result<Void> updateLostFoundStatus(@PathVariable Long id, @RequestParam String status) {
        adminService.updateLostFoundStatus(id, status);
        return Result.success();
    }
}
