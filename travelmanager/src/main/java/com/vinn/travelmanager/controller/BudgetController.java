package com.vinn.travelmanager.controller;

import com.vinn.travelmanager.common.Result;
import com.vinn.travelmanager.common.ResultCode;
import com.vinn.travelmanager.entity.Budget;
import com.vinn.travelmanager.entity.Plan;
import com.vinn.travelmanager.mapper.BudgetMapper;
import com.vinn.travelmanager.service.AIService;
import com.vinn.travelmanager.service.PlanService;
import com.vinn.travelmanager.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/budget")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BudgetController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PlanService planService;

    @Autowired
    private BudgetMapper budgetMapper;

    @Autowired
    private AIService aiService;

    @GetMapping("/analyze")
    public Result<Map<String, Object>> analyze(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(ResultCode.UNAUTHORIZED.getCode(), "请先登录");
        }

        // 1) 拉取用户的计划与预算
        List<Plan> plans = planService.getPlansByUserId(userId);
        Map<String, BigDecimal> totalsByCategory = new LinkedHashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Plan plan : plans) {
            List<Budget> budgets = budgetMapper.selectByPlanId(plan.getId());
            for (Budget b : budgets) {
                String cat = b.getCategory() == null ? "未分类" : b.getCategory();
                BigDecimal amt = b.getAmount() == null ? BigDecimal.ZERO : b.getAmount();
                totalsByCategory.put(cat, totalsByCategory.getOrDefault(cat, BigDecimal.ZERO).add(amt));
                totalAmount = totalAmount.add(amt);
            }
        }

        // 2) 构建给大模型的提示词
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一名旅行财务顾问。请基于以下历史预算数据，给出专业的中文分析与建议。\n");
        prompt.append("数据说明：金额单位为元；按类别汇总如下。\n\n");
        prompt.append("类别汇总：\n");
        for (Map.Entry<String, BigDecimal> e : totalsByCategory.entrySet()) {
            prompt.append("- ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        prompt.append("\n总支出：").append(totalAmount).append(" 元\n");
        prompt.append("计划数量：").append(plans.size()).append("\n\n");
        prompt.append("请产出一段200-300字的中文文字评价，包含：\n");
        prompt.append("1) 总体消费结构分析；2) 与常见出行开销对比（若不确定可做一般性建议）；\n");
        prompt.append("3) 可优化的类别与建议的调整比例；4) 未来预算分配建议（交通/住宿/餐饮/景点/购物等）。\n");
        prompt.append("只返回自然语言文字，不要代码块。\n");

        String aiComment = aiService.analyzeBudget(prompt.toString());

        Map<String, Object> data = new HashMap<>();
        data.put("totalsByCategory", totalsByCategory);
        data.put("totalAmount", totalAmount);
        data.put("plansCount", plans.size());
        data.put("aiComment", aiComment);

        return Result.success(data);
    }

    private Long getUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}


