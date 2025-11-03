package com.vinn.travelmanager.controller;

import com.vinn.travelmanager.common.Result;
import com.vinn.travelmanager.common.ResultCode;
import com.vinn.travelmanager.dto.UserPreferenceDTO;
import com.vinn.travelmanager.entity.UserPreference;
import com.vinn.travelmanager.service.UserPreferenceService;
import com.vinn.travelmanager.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户偏好控制器
 */
@RestController
@RequestMapping("/api/preference")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserPreferenceController {

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 保存或更新用户偏好
     */
    @PostMapping("/save")
    public Result<UserPreference> savePreference(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Valid @RequestBody UserPreferenceDTO preferenceDTO) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(ResultCode.UNAUTHORIZED.getCode(), "请先登录");
            }

            UserPreference preference = userPreferenceService.saveOrUpdatePreference(userId, preferenceDTO);
            return Result.success("偏好设置成功", preference);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户偏好
     */
    @GetMapping
    public Result<UserPreference> getPreference(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(ResultCode.UNAUTHORIZED.getCode(), "请先登录");
            }

            UserPreference preference = userPreferenceService.getPreferenceByUserId(userId);
            if (preference == null) {
                return Result.error(ResultCode.NOT_FOUND.getCode(), "用户偏好不存在");
            }

            return Result.success(preference);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从token中获取用户ID
     */
    private Long getUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            // 移除 "Bearer " 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}

