package com.example.demo.dto.statistics;

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
    private float totalSugar;
    private int totalNatrium;
    private float totalCholesterol;
    private float totalSaturatedFat;
    private float totalTransFat;
    private float weight;
    private int totalBurnedKcal;  // 소비 칼로리
    private int totalExerciseTime;
    private int recommendKcal;
    private boolean isAchieved;   // 달성 여부
    private int cheatingKcal; // 치팅 칼로리
    private float goalWeight; // 목표 체중
    private String target; // 목표
} 