package com.vinn.travelmanager.service;

import com.vinn.travelmanager.dto.PlanDetailDTO;
import com.vinn.travelmanager.dto.PlanSaveDTO;
import com.vinn.travelmanager.entity.Plan;

import java.util.List;

/**
 * 旅行计划服务接口
 */
public interface PlanService {
    /**
     * 保存或更新旅行计划（包含景点和预算）
     */
    Plan savePlan(Long userId, PlanSaveDTO planSaveDTO);

    /**
     * 根据ID查询旅行计划详情（包含景点和预算）
     */
    PlanDetailDTO getPlanDetailById(Long id);

    /**
     * 根据用户ID查询计划列表
     */
    List<Plan> getPlansByUserId(Long userId);

    /**
     * 删除计划
     */
    boolean deletePlan(Long userId, Long planId);
}

