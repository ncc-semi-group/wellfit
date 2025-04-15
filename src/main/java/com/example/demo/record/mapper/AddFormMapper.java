package com.example.demo.record.mapper;

import com.example.demo.dto.record.FoodNutritionDto;
import com.example.demo.dto.statistics.DailyStatisticsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AddFormMapper {
    
    void addFoodNutrition(FoodNutritionDto foodNutrition);
    
    FoodNutritionDto getFoodNutritionById(int foodId);
    
    void updateFoodNutrition(FoodNutritionDto foodNutrition);
    
    void deleteFoodNutrition(int foodId);
    
    void deleteFoodFavorites(int foodId);
    
    boolean checkFoodItemExists(Map<String, Integer> params);
    
}
