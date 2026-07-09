package com.campus.module.navigation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 地图节点实体（路口或建筑）
 */
@Data
@TableName("map_node")
public class MapNode {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String type;        // CROSSROAD / BUILDING / OTHER
    private Double x;
    private Double y;
    private String description;
}
