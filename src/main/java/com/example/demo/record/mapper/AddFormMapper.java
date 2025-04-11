package com.example.demo.record.mapper;

import com.example.demo.dto.FoodNutritionDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddFormMapper {
    
    void addFoodNutrition(FoodNutritionDto foodNutrition);
    
    FoodNutritionDto getFoodNutritionById(int foodId);
    
    void updateFoodNutrition(FoodNutritionDto foodNutrition);
    
    void deleteFoodNutrition(int foodId);
    
    void deleteFoodFavorites(int foodId);
}
