package com.campus.module.navigation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 路径规划请求
 */
@Data
public class RouteRequest {

    @NotNull(message = "起点节点ID不能为空")
    private Long startNodeId;

    @NotNull(message = "终点节点ID不能为空")
    private Long endNodeId;
}
