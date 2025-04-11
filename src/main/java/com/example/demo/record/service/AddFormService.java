package com.example.demo.record.service;

import com.example.demo.dto.record.FoodNutritionDto;
import com.example.demo.record.mapper.AddFormMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddFormService {
    
    private final AddFormMapper addFormMapper;
    
    @Transactional
    public void addFoodNutrition(FoodNutritionDto foodNutrition) {
        addFormMapper.addFoodNutrition(foodNutrition);
    }
    
    @Transactional
    public FoodNutritionDto getFoodNutritionById(int foodId) {
        return addFormMapper.getFoodNutritionById(foodId);
    }
    
    @Transactional
    public void updateFoodNutrition(FoodNutritionDto foodNutrition) {
        addFormMapper.updateFoodNutrition(foodNutrition);
    }
    
    @Transactional
    public void deleteFoodNutrition(int foodId, int userId) {
        addFormMapper.deleteFoodNutrition(foodId);
        // 즐겨찾기에서도 삭제
        addFormMapper.deleteFoodFavorites(foodId);
    }
}
