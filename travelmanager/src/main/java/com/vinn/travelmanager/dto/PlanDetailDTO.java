package com.vinn.travelmanager.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 旅行计划详情DTO（包含完整的景点和预算信息）
 */
@Data
public class PlanDetailDTO {
    private Long id;
    private Long userId;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private BigDecimal budget;
    private LocalDateTime createTime;
    private List<SpotDetailDTO> spots;
    private List<BudgetDetailDTO> budgets;

    @Data
    public static class SpotDetailDTO {
        private Long id;
        private Integer dayNumber;
        private String name;
        private String location;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private LocalDateTime createTime;
    }

    @Data
    public static class BudgetDetailDTO {
        private Long id;
        private String category;
        private BigDecimal amount;
        private LocalDateTime createTime;
    }
}

