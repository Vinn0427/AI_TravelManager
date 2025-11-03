package com.vinn.travelmanager.controller;

import com.vinn.travelmanager.common.Result;
import com.vinn.travelmanager.dto.AIGenerateRequestDTO;
import com.vinn.travelmanager.dto.PlanSaveDTO;
import com.vinn.travelmanager.service.AIService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI控制器
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * 生成旅行计划
     */
    @PostMapping("/generate-plan")
    public Result<PlanSaveDTO> generatePlan(@Valid @RequestBody AIGenerateRequestDTO request) {
        try {
            PlanSaveDTO planDTO = aiService.generateTravelPlan(request);
            
            // 日志记录返回的数据（用于调试）
            if (planDTO.getDailyGuides() != null) {
                org.slf4j.LoggerFactory.getLogger(AIController.class)
                    .info("返回的dailyGuides数量: {}", planDTO.getDailyGuides().size());
            } else {
                org.slf4j.LoggerFactory.getLogger(AIController.class)
                    .warn("返回的dailyGuides为null");
            }
            
            return Result.success("旅行计划生成成功", planDTO);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(AIController.class)
                .error("生成旅行计划失败", e);
            return Result.error("生成旅行计划失败: " + e.getMessage());
        }
    }
}

