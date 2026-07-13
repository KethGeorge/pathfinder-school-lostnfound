package com.campus.module.admin.dto;

import lombok.Data;

/**
 * 数据看板 VO
 */
@Data
public class DashboardVO {

    private Long totalUsers;
    private Long totalLostFounds;
    private Long todayNewUsers;
    private Long todayNewLostFounds;

    private Long openLostFounds;
    private Long closedLostFounds;
    private Long bannedUsers;
}
