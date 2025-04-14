package com.example.demo.record.mapper;

import com.example.demo.dto.statistics.DailyStatisticsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticsMapper {
    
    List<DailyStatisticsDto> getDailyStatistics(DailyStatisticsDto dailyStatistics);
    
    List<DailyStatisticsDto> getWeeklyAverageStatistics(DailyStatisticsDto dailyStatistics);
    
    List<DailyStatisticsDto> getMonthlyAverageStatistics(DailyStatisticsDto dailyStatistics);
    
}
