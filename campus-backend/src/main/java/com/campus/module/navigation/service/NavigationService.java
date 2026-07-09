package com.campus.module.navigation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.exception.BusinessException;
import com.campus.module.navigation.dto.RouteResponse;
import com.campus.module.navigation.entity.MapEdge;
import com.campus.module.navigation.entity.MapNode;
import com.campus.module.navigation.mapper.MapEdgeMapper;
import com.campus.module.navigation.mapper.MapNodeMapper;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 导航服务：建筑列表、搜索、寻路（Dijkstra）、坐标吸附
 */
@Service
public class NavigationService {

    private final MapNodeMapper nodeMapper;
    private final MapEdgeMapper edgeMapper;

    public NavigationService(MapNodeMapper nodeMapper, MapEdgeMapper edgeMapper) {
        this.nodeMapper = nodeMapper;
        this.edgeMapper = edgeMapper;
    }

    /** 获取所有建筑节点（不含路口） */
    public List<MapNode> getBuildings() {
        LambdaQueryWrapper<MapNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(MapNode::getType, "CROSSROAD")
               .orderByAsc(MapNode::getId);
        return nodeMapper.selectList(wrapper);
    }

    /** 搜索节点（按名称模糊匹配） */
    public List<MapNode> searchNodes(String keyword) {
        LambdaQueryWrapper<MapNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(MapNode::getName, keyword)
               .orderByAsc(MapNode::getId);
        return nodeMapper.selectList(wrapper);
    }

    /** 根据像素坐标吸附到最近节点 */
    public MapNode findNearestNode(double x, double y) {
        List<MapNode> allNodes = nodeMapper.selectList(null);
        if (allNodes.isEmpty()) {
            throw new BusinessException("地图数据为空");
        }

        MapNode nearest = null;
        double minDistSq = Double.MAX_VALUE;  // 注意：吸附用平方距离没问题（单次比较，不累加）
        for (MapNode node : allNodes) {
            double dx = node.getX() - x;
            double dy = node.getY() - y;
            double distSq = dx * dx + dy * dy;
            if (distSq < minDistSq) {
                minDistSq = distSq;
                nearest = node;
            }
        }
        return nearest;
    }

    /** Dijkstra 最短路径（步行模式，全图可用） */
    public RouteResponse findRoute(Long startId, Long endId) {
        if (startId.equals(endId)) {
            throw new BusinessException("起点和终点相同");
        }

        // 加载所有节点和边
        List<MapNode> allNodes = nodeMapper.selectList(null);
        List<MapEdge> allEdges = edgeMapper.selectList(null);

        // 构建 id->node 映射
        Map<Long, MapNode> nodeMap = new HashMap<>();
        for (MapNode node : allNodes) {
            nodeMap.put(node.getId(), node);
        }

        if (!nodeMap.containsKey(startId) || !nodeMap.containsKey(endId)) {
            throw new BusinessException("起点或终点不存在");
        }

        // 构建邻接表，边权用真实欧氏距离（修正 bug：不用平方）
        Map<Long, List<Edge>> graph = new HashMap<>();
        for (MapEdge e : allEdges) {
            MapNode from = nodeMap.get(e.getFromNode());
            MapNode to = nodeMap.get(e.getToNode());
            if (from == null || to == null) continue;

            double dist = calculateDistance(from, to);
            graph.computeIfAbsent(e.getFromNode(), k -> new ArrayList<>()).add(new Edge(e.getToNode(), dist));
            graph.computeIfAbsent(e.getToNode(), k -> new ArrayList<>()).add(new Edge(e.getFromNode(), dist));
        }

        // Dijkstra 算法
        Map<Long, Double> distance = new HashMap<>();
        Map<Long, Long> prev = new HashMap<>();
        Set<Long> visited = new HashSet<>();
        PriorityQueue<NodeDist> pq = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.dist));

        for (Long id : nodeMap.keySet()) {
            distance.put(id, Double.MAX_VALUE);
        }
        distance.put(startId, 0.0);
        pq.offer(new NodeDist(startId, 0.0));

        while (!pq.isEmpty()) {
            NodeDist current = pq.poll();
            Long u = current.nodeId;

            if (visited.contains(u)) continue;
            visited.add(u);

            if (u.equals(endId)) break;  // 终点已找到

            List<Edge> neighbors = graph.getOrDefault(u, Collections.emptyList());
            for (Edge edge : neighbors) {
                Long v = edge.to;
                if (visited.contains(v)) continue;

                double newDist = distance.get(u) + edge.weight;
                if (newDist < distance.get(v)) {
                    distance.put(v, newDist);
                    prev.put(v, u);
                    pq.offer(new NodeDist(v, newDist));
                }
            }
        }

        if (!prev.containsKey(endId) && !startId.equals(endId)) {
            throw new BusinessException("起点和终点之间无可达路径");
        }

        // 回溯路径
        List<Long> path = new ArrayList<>();
        Long cur = endId;
        while (cur != null) {
            path.add(cur);
            cur = prev.get(cur);
        }
        Collections.reverse(path);

        return new RouteResponse(path, distance.get(endId), path.size());
    }

    /** 计算两节点间真实欧氏距离（关键修正：开根号） */
    private double calculateDistance(MapNode a, MapNode b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);  // 真实距离，不是平方
    }

    // 内部类：邻接表的边
    private static class Edge {
        Long to;
        double weight;
        Edge(Long to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    // 内部类：优先队列节点
    private static class NodeDist {
        Long nodeId;
        double dist;
        NodeDist(Long nodeId, double dist) {
            this.nodeId = nodeId;
            this.dist = dist;
        }
    }
}
