package com.vinn.travelmanager.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预算实体类
 */
@Data
public class Budget {
    /**
     * 预算ID
     */
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 预算类别（如：交通、住宿、餐饮、景点、购物等）
     */
    private String category;

    /**
     * 预算金额（元）
     */
    private BigDecimal amount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

