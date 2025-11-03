package com.vinn.travelmanager.controller;

import com.vinn.travelmanager.common.Result;
import com.vinn.travelmanager.common.ResultCode;
import com.vinn.travelmanager.dto.PlanDetailDTO;
import com.vinn.travelmanager.dto.PlanSaveDTO;
import com.vinn.travelmanager.entity.Plan;
import com.vinn.travelmanager.service.PlanService;
import com.vinn.travelmanager.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 旅行计划控制器
 */
@RestController
@RequestMapping("/api/plan")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PlanController {

    @Autowired
    private PlanService planService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 保存或更新旅行计划
     */
    @PostMapping("/save")
    public Result<Map<String, Object>> savePlan(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Valid @RequestBody PlanSaveDTO planSaveDTO) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(ResultCode.UNAUTHORIZED.getCode(), "请先登录");
            }

            Plan plan = planService.savePlan(userId, planSaveDTO);
            Map<String, Object> data = new HashMap<>();
            data.put("plan", plan);
            data.put("planId", plan.getId());

            return Result.success(planSaveDTO.getId() != null ? "行程更新成功" : "行程保存成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取旅行计划详情（包含景点和预算）
     */
    @GetMapping("/{id}")
    public Result<PlanDetailDTO> getPlanDetail(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(ResultCode.UNAUTHORIZED.getCode(), "请先登录");
            }

            PlanDetailDTO planDetail = planService.getPlanDetailById(id);
            if (planDetail == null) {
                return Result.error(ResultCode.NOT_FOUND.getCode(), "旅行计划不存在");
            }

            // 验证计划是否属于当前用户
            if (!planDetail.getUserId().equals(userId)) {
                return Result.error(ResultCode.FORBIDDEN.getCode(), "无权访问此计划");
            }

            return Result.success(planDetail);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户的旅行计划列表
     */
    @GetMapping("/list")
    public Result<List<Plan>> getPlans(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(ResultCode.UNAUTHORIZED.getCode(), "请先登录");
            }

            List<Plan> plans = planService.getPlansByUserId(userId);
            return Result.success(plans);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除旅行计划
     */
    @DeleteMapping("/{id}")
    public Result<?> deletePlan(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(ResultCode.UNAUTHORIZED.getCode(), "请先登录");
            }

            boolean success = planService.deletePlan(userId, id);
            if (success) {
                return Result.success("行程删除成功");
            } else {
                return Result.error("行程删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从token中获取用户ID
     */
    private Long getUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            // 移除 "Bearer " 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}

