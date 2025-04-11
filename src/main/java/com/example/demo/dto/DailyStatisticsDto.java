package com.example.demo.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import java.time.LocalDate;

@Data
@Alias("DailyStatisticsDto")
public class DailyStatisticsDto {
    private int id;
    private int userId;
    private LocalDate date;
    private int totalKcal;        // 섭취 칼로리
    private float totalProtein;
    private float totalFat;
    private float totalCarbohydrate;
    private int totalNatrium;
    private float totalCholesterol;
    private float totalSaturatedFat;
    private float totalTransFat;
    private float weight;
    private int totalBurnedKcal;  // 소비 칼로리
    private int totalExerciseTime;
    private int recommendedKcal;
    private boolean isAchieved;   // 달성 여부
} 