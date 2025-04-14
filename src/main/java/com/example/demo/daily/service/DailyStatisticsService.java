package com.example.demo.daily.service;

import com.example.demo.daily.mapper.DailyStatisticsMapper;
import com.example.demo.dto.statistics.DailyStatisticsDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DailyStatisticsService {
    
    private final DailyStatisticsMapper dailyStatisticsMapper;
    
    public DailyStatisticsDto getDailyStatistics(int userId, LocalDate date) {
        return dailyStatisticsMapper.getDailyStatistics(userId, date);
    }
    
    public List<Map<String, Object>> getMonthlyStatistics(int userId, int year, int month) {
        return dailyStatisticsMapper.getMonthlyStatistics(userId, year, month);
    }
    
    public void updateDailyStatistics(DailyStatisticsDto statistics) {
        // 성공 배지 조건 체크
        boolean isAchieved = statistics.getTotalBurnedKcal() > statistics.getTotalKcal();
        statistics.setAchieved(isAchieved);
        
        dailyStatisticsMapper.updateDailyStatistics(statistics);
    }
} 