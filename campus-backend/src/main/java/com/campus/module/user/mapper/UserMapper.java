package com.campus.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper，继承 MyBatis-Plus BaseMapper 获得基础 CRUD
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
