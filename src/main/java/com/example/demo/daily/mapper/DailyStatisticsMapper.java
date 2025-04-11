package com.example.demo.daily.mapper;

import com.example.demo.dto.DailyStatisticsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface DailyStatisticsMapper {
    // 특정 사용자의 특정 날짜 통계 조회
    DailyStatisticsDto getDailyStatistics(@Param("userId") int userId, @Param("date") LocalDate date);
    
    // 특정 사용자의 특정 월의 통계 목록 조회
    List<Map<String, Object>> getMonthlyStatistics(@Param("userId") int userId, 
                                                  @Param("year") int year, 
                                                  @Param("month") int month);
    
    // 통계 업데이트 (달성 여부 포함)
    void updateDailyStatistics(DailyStatisticsDto statistics);
} 