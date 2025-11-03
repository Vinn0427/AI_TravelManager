package com.vinn.travelmanager.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户偏好实体类
 */
@Data
public class UserPreference {
    /**
     * 偏好ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 旅行风格（如：休闲、冒险、文化、购物等）
     */
    private String travelStyle;

    /**
     * 预算范围（如：经济型、中等、豪华）
     */
    private String budgetRange;

    /**
     * 偏好交通方式（如：飞机、高铁、自驾等）
     */
    private String preferredTransport;

    /**
     * 偏好住宿类型（如：酒店、民宿、青旅等）
     */
    private String preferredAccommodation;

    /**
     * 饮食偏好（如：素食、海鲜、当地特色等）
     */
    private String dietaryPreference;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

