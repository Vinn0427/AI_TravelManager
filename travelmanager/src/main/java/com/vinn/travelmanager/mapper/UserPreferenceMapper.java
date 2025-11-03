package com.vinn.travelmanager.mapper;

import com.vinn.travelmanager.entity.UserPreference;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户偏好Mapper接口
 */
@Mapper
public interface UserPreferenceMapper {
    /**
     * 插入用户偏好
     */
    int insert(UserPreference userPreference);

    /**
     * 更新用户偏好
     */
    int update(UserPreference userPreference);

    /**
     * 根据用户ID查询偏好
     */
    UserPreference selectByUserId(@Param("userId") Long userId);

    /**
     * 删除用户偏好
     */
    int deleteByUserId(@Param("userId") Long userId);
}

