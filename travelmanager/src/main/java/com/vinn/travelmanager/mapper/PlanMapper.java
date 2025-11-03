package com.vinn.travelmanager.mapper;

import com.vinn.travelmanager.entity.Plan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 旅行计划Mapper接口
 */
@Mapper
public interface PlanMapper {
    /**
     * 插入计划
     */
    int insert(Plan plan);

    /**
     * 更新计划
     */
    int update(Plan plan);

    /**
     * 根据ID查询计划
     */
    Plan selectById(Long id);

    /**
     * 根据用户ID查询计划列表
     */
    List<Plan> selectByUserId(@Param("userId") Long userId);

    /**
     * 删除计划
     */
    int deleteById(@Param("id") Long id);
}

