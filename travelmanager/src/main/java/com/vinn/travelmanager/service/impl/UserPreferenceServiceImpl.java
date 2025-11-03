package com.vinn.travelmanager.service.impl;

import com.vinn.travelmanager.dto.UserPreferenceDTO;
import com.vinn.travelmanager.entity.UserPreference;
import com.vinn.travelmanager.mapper.UserPreferenceMapper;
import com.vinn.travelmanager.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户偏好服务实现类
 */
@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {

    @Autowired
    private UserPreferenceMapper userPreferenceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserPreference saveOrUpdatePreference(Long userId, UserPreferenceDTO preferenceDTO) {
        UserPreference existingPreference = userPreferenceMapper.selectByUserId(userId);

        if (existingPreference != null) {
            // 更新现有偏好
            existingPreference.setTravelStyle(preferenceDTO.getTravelStyle());
            existingPreference.setBudgetRange(preferenceDTO.getBudgetRange());
            existingPreference.setPreferredTransport(preferenceDTO.getPreferredTransport());
            existingPreference.setPreferredAccommodation(preferenceDTO.getPreferredAccommodation());
            existingPreference.setDietaryPreference(preferenceDTO.getDietaryPreference());
            userPreferenceMapper.update(existingPreference);
            return userPreferenceMapper.selectByUserId(userId);
        } else {
            // 创建新偏好
            UserPreference newPreference = new UserPreference();
            newPreference.setUserId(userId);
            newPreference.setTravelStyle(preferenceDTO.getTravelStyle());
            newPreference.setBudgetRange(preferenceDTO.getBudgetRange());
            newPreference.setPreferredTransport(preferenceDTO.getPreferredTransport());
            newPreference.setPreferredAccommodation(preferenceDTO.getPreferredAccommodation());
            newPreference.setDietaryPreference(preferenceDTO.getDietaryPreference());
            userPreferenceMapper.insert(newPreference);
            return newPreference;
        }
    }

    @Override
    public UserPreference getPreferenceByUserId(Long userId) {
        return userPreferenceMapper.selectByUserId(userId);
    }
}

