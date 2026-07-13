package com.campus.common;

import lombok.Data;

/**
 * 分页请求基类
 */
@Data
public class PageRequest {

    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
