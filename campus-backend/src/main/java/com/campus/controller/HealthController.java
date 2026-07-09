package com.campus.controller;

import com.campus.common.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查：验证服务与数据库是否正常
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        // 顺带验证数据库连通性
        try {
            Integer nodeCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM map_node", Integer.class);
            data.put("db", "connected");
            data.put("mapNodeCount", nodeCount);
        } catch (Exception e) {
            data.put("db", "error: " + e.getMessage());
        }
        return Result.success(data);
    }
}
