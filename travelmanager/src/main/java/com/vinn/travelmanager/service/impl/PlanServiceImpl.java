package com.vinn.travelmanager.service.impl;

import com.vinn.travelmanager.dto.PlanDetailDTO;
import com.vinn.travelmanager.dto.PlanSaveDTO;
import com.vinn.travelmanager.entity.Budget;
import com.vinn.travelmanager.entity.Plan;
import com.vinn.travelmanager.entity.Spot;
import com.vinn.travelmanager.mapper.BudgetMapper;
import com.vinn.travelmanager.mapper.PlanMapper;
import com.vinn.travelmanager.mapper.SpotMapper;
import com.vinn.travelmanager.service.PlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 旅行计划服务实现类
 */
@Service
public class PlanServiceImpl implements PlanService {

    @Autowired
    private PlanMapper planMapper;

    @Autowired
    private SpotMapper spotMapper;

    @Autowired
    private BudgetMapper budgetMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Plan savePlan(Long userId, PlanSaveDTO planSaveDTO) {
        Plan plan = new Plan();
        plan.setUserId(userId);
        plan.setDestination(planSaveDTO.getDestination());
        plan.setStartDate(planSaveDTO.getStartDate());
        plan.setEndDate(planSaveDTO.getEndDate());
        plan.setTotalDays(planSaveDTO.getTotalDays());
        plan.setBudget(planSaveDTO.getBudget());

        if (planSaveDTO.getId() != null) {
            // 更新计划
            plan.setId(planSaveDTO.getId());
            planMapper.update(plan);

            // 删除旧的景点和预算
            spotMapper.deleteByPlanId(planSaveDTO.getId());
            budgetMapper.deleteByPlanId(planSaveDTO.getId());
        } else {
            // 新建计划
            planMapper.insert(plan);
        }

        Long planId = plan.getId();

        // 保存景点
        if (planSaveDTO.getSpots() != null && !planSaveDTO.getSpots().isEmpty()) {
            List<Spot> spots = planSaveDTO.getSpots().stream()
                .map(spotDTO -> {
                    Spot spot = new Spot();
                    spot.setPlanId(planId);
                    spot.setDayNumber(spotDTO.getDayNumber());
                    spot.setName(spotDTO.getName());
                    spot.setLocation(spotDTO.getLocation());
                    spot.setLatitude(spotDTO.getLatitude());
                    spot.setLongitude(spotDTO.getLongitude());
                    return spot;
                })
                .collect(Collectors.toList());

            if (spots.size() > 0) {
                spotMapper.batchInsert(spots);
            }
        }

        // 保存预算
        if (planSaveDTO.getBudgets() != null && !planSaveDTO.getBudgets().isEmpty()) {
            List<Budget> budgets = planSaveDTO.getBudgets().stream()
                .map(budgetDTO -> {
                    Budget budget = new Budget();
                    budget.setPlanId(planId);
                    budget.setCategory(budgetDTO.getCategory());
                    budget.setAmount(budgetDTO.getAmount());
                    return budget;
                })
                .collect(Collectors.toList());

            if (budgets.size() > 0) {
                budgetMapper.batchInsert(budgets);
            }
        }

        return planMapper.selectById(planId);
    }

    @Override
    public PlanDetailDTO getPlanDetailById(Long id) {
        Plan plan = planMapper.selectById(id);
        if (plan == null) {
            return null;
        }

        PlanDetailDTO detailDTO = new PlanDetailDTO();
        BeanUtils.copyProperties(plan, detailDTO);

        // 查询景点
        List<Spot> spots = spotMapper.selectByPlanId(id);
        List<PlanDetailDTO.SpotDetailDTO> spotDetails = spots.stream()
            .map(spot -> {
                PlanDetailDTO.SpotDetailDTO dto = new PlanDetailDTO.SpotDetailDTO();
                BeanUtils.copyProperties(spot, dto);
                return dto;
            })
            .collect(Collectors.toList());
        detailDTO.setSpots(spotDetails);

        // 查询预算
        List<Budget> budgets = budgetMapper.selectByPlanId(id);
        List<PlanDetailDTO.BudgetDetailDTO> budgetDetails = budgets.stream()
            .map(budget -> {
                PlanDetailDTO.BudgetDetailDTO dto = new PlanDetailDTO.BudgetDetailDTO();
                BeanUtils.copyProperties(budget, dto);
                return dto;
            })
            .collect(Collectors.toList());
        detailDTO.setBudgets(budgetDetails);

        return detailDTO;
    }

    @Override
    public List<Plan> getPlansByUserId(Long userId) {
        return planMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePlan(Long userId, Long planId) {
        Plan plan = planMapper.selectById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            return false;
        }

        // 由于设置了级联删除，直接删除plan即可自动删除关联的spots和budgets
        planMapper.deleteById(planId);
        return true;
    }
}

