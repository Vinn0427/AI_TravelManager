package com.vinn.travelmanager.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DailyGuide {
    private Long id;
    private Long userId;
    private Long planId;
    private Integer dayNumber;
    private String guideText;
    private LocalDateTime createTime;
}


