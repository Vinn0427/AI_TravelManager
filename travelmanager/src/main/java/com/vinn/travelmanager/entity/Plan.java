package com.vinn.travelmanager.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 旅行计划实体类
 */
@Data
public class Plan {
    /**
     * 计划ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 出发日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 总天数
     */
    private Integer totalDays;

    /**
     * 总预算（元）
     */
    private BigDecimal budget;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

