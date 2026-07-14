package com.campus.module.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.exception.BusinessException;
import com.campus.module.admin.dto.*;
import com.campus.module.lostfound.entity.LostFound;
import com.campus.module.lostfound.mapper.LostFoundMapper;
import com.campus.module.user.entity.User;
import com.campus.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 管理后台服务
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserMapper userMapper;
    private final LostFoundMapper lostFoundMapper;

    /**
     * 获取数据看板统计
     */
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();

        // 总用户数
        vo.setTotalUsers(userMapper.selectCount(null));

        // 总失物招领数
        vo.setTotalLostFounds(lostFoundMapper.selectCount(null));

        // 今日新增用户
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.ge(User::getCreatedAt, todayStart);
        vo.setTodayNewUsers(userMapper.selectCount(userWrapper));

        // 今日新增失物招领
        LambdaQueryWrapper<LostFound> lostFoundWrapper = new LambdaQueryWrapper<>();
        lostFoundWrapper.ge(LostFound::getCreatedAt, todayStart);
        vo.setTodayNewLostFounds(lostFoundMapper.selectCount(lostFoundWrapper));

        // 未解决的失物招领
        LambdaQueryWrapper<LostFound> openWrapper = new LambdaQueryWrapper<>();
        openWrapper.eq(LostFound::getStatus, "OPEN");
        vo.setOpenLostFounds(lostFoundMapper.selectCount(openWrapper));

        // 已解决的失物招领
        LambdaQueryWrapper<LostFound> closedWrapper = new LambdaQueryWrapper<>();
        closedWrapper.eq(LostFound::getStatus, "CLOSED");
        vo.setClosedLostFounds(lostFoundMapper.selectCount(closedWrapper));

        // 被封禁的用户
        LambdaQueryWrapper<User> bannedWrapper = new LambdaQueryWrapper<>();
        bannedWrapper.eq(User::getIsBanned, 1);
        vo.setBannedUsers(userMapper.selectCount(bannedWrapper));

        return vo;
    }

    /**
     * 查询用户列表
     */
    public IPage<UserManageVO> queryUsers(UserQueryRequest request) {
        Page<User> page = new Page<>(request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w.like(User::getStudentId, request.getKeyword())
                    .or().like(User::getUsername, request.getKeyword())
                    .or().like(User::getPhone, request.getKeyword())
                    .or().like(User::getEmail, request.getKeyword()));
        }

        // 角色筛选
        if (StringUtils.hasText(request.getRole())) {
            wrapper.eq(User::getRole, request.getRole());
        }

        // 封禁状态筛选
        if (request.getIsBanned() != null) {
            wrapper.eq(User::getIsBanned, request.getIsBanned());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(User::getCreatedAt);

        IPage<User> userPage = userMapper.selectPage(page, wrapper);

        // 转换为 VO
        return userPage.convert(user -> {
            UserManageVO vo = new UserManageVO();
            vo.setId(user.getId());
            vo.setStudentId(user.getStudentId());
            vo.setUsername(user.getUsername());
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
            vo.setRole(user.getRole());
            vo.setIsBanned(user.getIsBanned());
            vo.setCreatedAt(user.getCreatedAt());
            vo.setUpdatedAt(user.getUpdatedAt());

            // 统计该用户的失物招领数量
            LambdaQueryWrapper<LostFound> lostFoundWrapper = new LambdaQueryWrapper<>();
            lostFoundWrapper.eq(LostFound::getUserId, user.getId());
            vo.setLostFoundCount(lostFoundMapper.selectCount(lostFoundWrapper));

            return vo;
        });
    }

    /**
     * 封禁/解封用户
     */
    public void banUser(Long userId, Integer isBanned) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 不能封禁管理员
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException("不能封禁管理员账号");
        }

        user.setIsBanned(isBanned);
        userMapper.updateById(user);
    }

    /**
     * 管理员强制删除失物招领
     */
    public void deleteLostFound(Long id) {
        LostFound lostFound = lostFoundMapper.selectById(id);
        if (lostFound == null) {
            throw new BusinessException("失物招领信息不存在");
        }

        lostFoundMapper.deleteById(id);
    }

    /**
     * 管理员修改失物招领状态
     */
    public void updateLostFoundStatus(Long id, String status) {
        LostFound lostFound = lostFoundMapper.selectById(id);
        if (lostFound == null) {
            throw new BusinessException("失物招领信息不存在");
        }

        lostFound.setStatus(status);
        lostFoundMapper.updateById(lostFound);
    }
}
