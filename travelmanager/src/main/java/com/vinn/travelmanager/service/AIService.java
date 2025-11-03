package com.vinn.travelmanager.service;

import com.vinn.travelmanager.dto.AIGenerateRequestDTO;
import com.vinn.travelmanager.dto.PlanSaveDTO;

/**
 * AI服务接口
 */
public interface AIService {
    /**
     * 调用阿里百炼大模型生成旅行计划
     * @param request 生成请求
     * @return 解析后的PlanSaveDTO，包含Plan、Spot、Budget等信息
     */
    PlanSaveDTO generateTravelPlan(AIGenerateRequestDTO request);

    /**
     * 预算分析：根据聚合后的预算数据生成文字评价
     * @param prompt 已构建好的提示词
     * @return 模型返回的中文评价
     */
    String analyzeBudget(String prompt);
}

