package com.example.demo.record.service;

import com.example.demo.dto.statistics.DailyStatisticsDto;
import com.example.demo.record.mapper.StatisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final StatisticsMapper statisticsMapper;
    
    
    public Map<String, Object> getUserStatistics(int userId) {
        
        Map<String, Object> result = new HashMap<>();
        DailyStatisticsDto dailyStatistics = new DailyStatisticsDto();
        dailyStatistics.setUserId(userId);
        dailyStatistics.setDate(LocalDate.now());
        
        // 일별 부터 시작
        String period = "daily";
        Map<String, Object> periodData = mapStatisticsData(dailyStatistics, period);
        result.put(period, periodData);
        
        // 주간 통계
        period = "weekly";
        periodData = mapStatisticsData(dailyStatistics, period);
        result.put(period, periodData);
        
        // 월간 통계
        period = "monthly";
        periodData = mapStatisticsData(dailyStatistics, period);
        result.put(period, periodData);
        
        return result;
    }
    
    private String formatDate(String dateStr, String period) {
        LocalDate date = LocalDate.parse(dateStr);
        if ("daily".equals(period)) {
            return date.format(DateTimeFormatter.ofPattern("MM.dd"));
        } else if ("weekly".equals(period)) {
            return "~" + date.format(DateTimeFormatter.ofPattern("MM.dd"));
        } else if ("monthly".equals(period)) {
            return date.format(DateTimeFormatter.ofPattern("yy.MM"));
        }
        return dateStr;
    }
    
    
    private Map<String, Object> mapStatisticsData(DailyStatisticsDto dailyStatistics, String period) {
        // 일별 통계 데이터 조회
        List<DailyStatisticsDto> statistics = new ArrayList<>();
        if ("daily".equals(period)) {
            statistics = statisticsMapper.getDailyStatistics(dailyStatistics);
        } else if ("weekly".equals(period)) {
            statistics = statisticsMapper.getWeeklyAverageStatistics(dailyStatistics);
        } else if ("monthly".equals(period)) {
            statistics = statisticsMapper.getMonthlyAverageStatistics(dailyStatistics);
        }
        
        // 결과 포맷 구성
        List<String> dates = statistics.stream()
                .map(stat -> formatDate(stat.getDate().toString(), period))
                .toList();
        
        List<Float> weights = statistics.stream()
                .map(stat -> {
                    float weight = stat.getWeight();
                    if (weight == 0f) return null;
                    // 소숫점 한 자리까지 반올림
                    return Math.round(weight * 10) / 10.0f;
                })
                .toList();
        
        List<Integer> intakes = statistics.stream()
                .map(stat -> {
                    int intake = stat.getTotalKcal();
                    if (intake == 0) return null;
                    return intake;
                })
                .toList();
        
        List<Integer> burned = statistics.stream()
                .map(stat -> {
                    int burn = stat.getTotalBurnedKcal();
                    if (burn == 0) return null;
                    return burn;
                })
                .toList();
        
        Map<String, Object> periodData = new HashMap<>();
        periodData.put("dates", dates);
        periodData.put("weights", weights);
        periodData.put("intake", intakes);
        periodData.put("burned", burned);
        
        return periodData;
    }
    
    
}