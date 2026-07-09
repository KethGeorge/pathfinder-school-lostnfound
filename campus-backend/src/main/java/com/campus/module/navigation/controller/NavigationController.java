package com.campus.module.navigation.controller;

import com.campus.common.Result;
import com.campus.module.navigation.dto.RouteRequest;
import com.campus.module.navigation.dto.RouteResponse;
import com.campus.module.navigation.entity.MapNode;
import com.campus.module.navigation.service.NavigationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 导航接口：建筑列表、搜索、寻路、坐标吸附
 */
@RestController
@RequestMapping("/api/navigation")
public class NavigationController {

    private final NavigationService navigationService;

    public NavigationController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    /** 获取所有建筑节点 */
    @GetMapping("/buildings")
    public Result<List<MapNode>> getBuildings() {
        return Result.success(navigationService.getBuildings());
    }

    /** 搜索节点（关键词模糊匹配） */
    @GetMapping("/search")
    public Result<List<MapNode>> search(@RequestParam String keyword) {
        return Result.success(navigationService.searchNodes(keyword));
    }

    /** 路径规划（步行模式） */
    @PostMapping("/route")
    public Result<RouteResponse> route(@Valid @RequestBody RouteRequest request) {
        RouteResponse response = navigationService.findRoute(request.getStartNodeId(), request.getEndNodeId());
        return Result.success(response);
    }

    /** 坐标吸附到最近节点 */
    @GetMapping("/nearest")
    public Result<MapNode> nearest(@RequestParam double x, @RequestParam double y) {
        MapNode node = navigationService.findNearestNode(x, y);
        return Result.success(node);
    }
}
