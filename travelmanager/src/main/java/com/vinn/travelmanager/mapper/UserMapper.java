package com.vinn.travelmanager.mapper;

import com.vinn.travelmanager.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     */
    User selectById(Long id);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    User selectByEmail(String email);

    /**
     * 根据手机号查询用户
     */
    User selectByPhone(String phone);

    /**
     * 根据用户名或邮箱查询用户（用于登录）
     */
    User selectByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户信息
     */
    int updateById(User user);

    /**
     * 根据ID删除用户（逻辑删除）
     */
    int deleteById(Long id);
}

