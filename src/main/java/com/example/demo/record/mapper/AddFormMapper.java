package com.example.demo.record.mapper;

import com.example.demo.dto.FoodNutritionDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddFormMapper {
    
    void addFoodNutrition(FoodNutritionDTO foodNutrition);
    
    FoodNutritionDTO getFoodNutritionById(int foodId);
    
    void updateFoodNutrition(FoodNutritionDTO foodNutrition);
    
    void deleteFoodNutrition(int foodId);
    
    void deleteFoodFavorites(int foodId);
}
