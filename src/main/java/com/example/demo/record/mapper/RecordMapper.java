package com.example.demo.record.mapper;

import com.example.demo.dto.record.FoodRecordsDto;
import com.example.demo.dto.statistics.DailyStatisticsDto;
import com.example.demo.dto.user.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface RecordMapper {
    
    List<FoodRecordsDto> getFoodRecords (FoodRecordsDto foodRecords);
    
    void updateDailyStatistics(DailyStatisticsDto dailyStatistics);
    
    UserDto getUserById(int userId);
    
    List<DailyStatisticsDto> getWeightInfoByStatistics(DailyStatisticsDto dailyStatistics);
    
    List<DailyStatisticsDto> getGoalWeightInfoByStatistics(Map<String, Date> params);
    
    void updateUserWeight(UserDto user);
    
    Integer getTotalKcal(FoodRecordsDto foodRecords);
    
    DailyStatisticsDto getArchivedDataByStatistics(FoodRecordsDto foodRecords);
    
    void updateAchieved(DailyStatisticsDto dailyStatistics);
    
    int getCheatingKcalByStatistics(Map<String, Object> params);
    
    void updateCheatingKcal(DailyStatisticsDto dailyStatistics);
    
    void updateCheatingDay(UserDto user);
    
    List<Integer> getAchievedUsersByStatistics(DailyStatisticsDto dailyStatistics);
    
}
