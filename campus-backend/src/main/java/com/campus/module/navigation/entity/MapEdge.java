package com.campus.module.navigation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 地图边实体
 */
@Data
@TableName("map_edge")
public class MapEdge {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long fromNode;
    private Long toNode;
    private String edgeType;        // ROAD / PATH
    private String accessibleBy;    // WALK / BOTH
    private Double distance;        // 可能为空，由算法计算
}
