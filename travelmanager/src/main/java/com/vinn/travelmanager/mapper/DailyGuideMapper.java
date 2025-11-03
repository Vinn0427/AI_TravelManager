package com.vinn.travelmanager.mapper;

import com.vinn.travelmanager.entity.DailyGuide;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DailyGuideMapper {
    int insert(DailyGuide guide);
    int batchInsert(@Param("list") List<DailyGuide> guides);
    List<DailyGuide> selectByPlanId(@Param("planId") Long planId);
    int deleteByPlanId(@Param("planId") Long planId);
}


