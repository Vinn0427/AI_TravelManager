package com.vinn.travelmanager.controller;

import com.vinn.travelmanager.common.Result;
import com.vinn.travelmanager.dto.LoginDTO;
import com.vinn.travelmanager.dto.RegisterDTO;
import com.vinn.travelmanager.entity.User;
import com.vinn.travelmanager.service.UserService;
import com.vinn.travelmanager.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            User user = userService.register(registerDTO);
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            return Result.success("注册成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.login(loginDTO);
            
            // 生成JWT Token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            
            return Result.success("登录成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        // TODO: 从token中解析用户信息
        // 目前先返回示例
        return Result.error("请先登录");
    }
}

