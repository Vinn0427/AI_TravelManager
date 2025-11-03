package com.vinn.travelmanager.mapper;

import com.vinn.travelmanager.entity.Spot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 景点Mapper接口
 */
@Mapper
public interface SpotMapper {
    /**
     * 插入景点
     */
    int insert(Spot spot);

    /**
     * 批量插入景点
     */
    int batchInsert(@Param("spots") List<Spot> spots);

    /**
     * 根据计划ID查询景点列表
     */
    List<Spot> selectByPlanId(@Param("planId") Long planId);

    /**
     * 根据计划ID和天数查询景点列表
     */
    List<Spot> selectByPlanIdAndDay(@Param("planId") Long planId, @Param("dayNumber") Integer dayNumber);

    /**
     * 删除计划的所有景点
     */
    int deleteByPlanId(@Param("planId") Long planId);

    /**
     * 删除景点
     */
    int deleteById(@Param("id") Long id);
}

