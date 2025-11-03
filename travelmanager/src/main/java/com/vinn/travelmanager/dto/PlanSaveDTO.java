package com.vinn.travelmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 旅行计划保存DTO
 */
@Data
public class PlanSaveDTO {
    /**
     * 计划ID（用于更新，新建时为空）
     */
    private Long id;

    @NotBlank(message = "目的地不能为空")
    private String destination;

    @NotNull(message = "出发日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @NotNull(message = "总天数不能为空")
    private Integer totalDays;

    @NotNull(message = "总预算不能为空")
    private BigDecimal budget;
    
    /**
     * 标题（用于展示，不保存到数据库）
     */
    private String title;

    /**
     * 景点列表
     */
    @Valid
    private List<SpotDTO> spots;

    /**
     * 预算列表
     */
    @Valid
    private List<BudgetDTO> budgets;

    /**
     * 每日路线指引列表
     */
    @Valid
    private List<DailyGuideDTO> dailyGuides;

    /**
     * 每日路线指引DTO
     */
    @Data
    public static class DailyGuideDTO {
        /**
         * 第几天（从1开始）
         */
        @NotNull(message = "天数不能为空")
        private Integer dayNumber;

        /**
         * 路线指引文本（包含交通、住宿、景点、餐厅的推荐）
         */
        @NotBlank(message = "路线指引不能为空")
        private String guide;
    }

    /**
     * 景点DTO
     */
    @Data
    public static class SpotDTO {
        private Integer dayNumber;

        @NotBlank(message = "景点名称不能为空")
        private String name;

        private String location;
        private java.math.BigDecimal latitude;
        private java.math.BigDecimal longitude;
    }

    /**
     * 预算DTO
     */
    @Data
    public static class BudgetDTO {
        @NotBlank(message = "预算类别不能为空")
        private String category;

        @NotNull(message = "预算金额不能为空")
        private BigDecimal amount;
    }
}

