package com.vinn.travelmanager.service;

import com.vinn.travelmanager.dto.UserPreferenceDTO;
import com.vinn.travelmanager.entity.UserPreference;

/**
 * 用户偏好服务接口
 */
public interface UserPreferenceService {
    /**
     * 保存或更新用户偏好
     */
    UserPreference saveOrUpdatePreference(Long userId, UserPreferenceDTO preferenceDTO);

    /**
     * 根据用户ID查询偏好
     */
    UserPreference getPreferenceByUserId(Long userId);
}

