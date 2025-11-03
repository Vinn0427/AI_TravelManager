package com.vinn.travelmanager.mapper;

import com.vinn.travelmanager.entity.Budget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预算Mapper接口
 */
@Mapper
public interface BudgetMapper {
    /**
     * 插入预算
     */
    int insert(Budget budget);

    /**
     * 批量插入预算
     */
    int batchInsert(@Param("budgets") List<Budget> budgets);

    /**
     * 根据计划ID查询预算列表
     */
    List<Budget> selectByPlanId(@Param("planId") Long planId);

    /**
     * 删除计划的所有预算
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 删除预算
     */
    int deleteById(@Param("id") Long id);
}

