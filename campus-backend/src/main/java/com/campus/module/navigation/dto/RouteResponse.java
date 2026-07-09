package com.campus.module.navigation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 路径规划响应
 */
@Data
@AllArgsConstructor
public class RouteResponse {

    private List<Long> path;        // 节点ID序列
    private Double totalDistance;   // 总距离（米）
    private Integer nodeCount;      // 经过节点数
}
