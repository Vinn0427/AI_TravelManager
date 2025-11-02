package com.vinn.travelmanager.service;

import com.vinn.travelmanager.dto.LoginDTO;
import com.vinn.travelmanager.dto.RegisterDTO;
import com.vinn.travelmanager.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    User register(RegisterDTO registerDTO);

    /**
     * 用户登录
     */
    User login(LoginDTO loginDTO);

    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);
}

