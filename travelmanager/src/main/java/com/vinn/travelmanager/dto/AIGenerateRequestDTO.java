package com.vinn.travelmanager.dto;

import lombok.Data;

/**
 * AI生成旅行计划请求DTO
 */
@Data
public class AIGenerateRequestDTO {
    /**
     * 目的地
     */
    private String destination;

    /**
     * 出发日期（YYYY-MM-DD）
     */
    private String startDate;

    /**
     * 旅行天数
     */
    private Integer days;

    /**
     * 预算（元）
     */
    private Double budget;

    /**
     * 同行人数
     */
    private Integer people;

    /**
     * 旅行偏好（列表）
     */
    private java.util.List<String> preferences;

    /**
     * 特殊需求
     */
    private String requirements;
}

