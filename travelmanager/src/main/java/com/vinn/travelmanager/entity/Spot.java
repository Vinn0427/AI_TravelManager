package com.vinn.travelmanager.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点实体类
 */
@Data
public class Spot {
    /**
     * 景点ID
     */
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 第几天（从1开始）
     */
    private Integer dayNumber;

    /**
     * 景点名称
     */
    private String name;

    /**
     * 位置描述
     */
    private String location;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

