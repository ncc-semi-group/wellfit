package com.example.demo.record.mapper;

import com.example.demo.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SearchMapper {

    Integer getFoodRecordsId (FoodRecordsDTO foodRecords);
    
    void addFoodRecords (FoodRecordsDTO foodRecords);
    
    int getRecordItemsCount (int foodRecordsId);
    
    void addRecordItems (RecordItemsDTO recordItems);
    
    List<FoodNutritionDTO> getUserFoodFavorites (int userId);
    
    int isFoodFavorite (FoodFavoritesDTO foodFavorites);
    
    void addFoodFavorites (FoodFavoritesDTO foodFavorites);
    
    void deleteFoodFavorites (FoodFavoritesDTO foodFavorites);
    
    List<FoodNutritionDTO> getIndividualFoodNutrition (int userId);
    
    void updateFoodRecordsKcal (FoodRecordsDTO foodRecords);
    
    int getStatisticsCount (Map<String, Object> params);
    
    void addDailyStatistics (Map<String, Object> params);
    
    // 사용자 ID로 템플릿 목록과 각 템플릿의 식품 이름 리스트 조회
    List<TemplateWithFoodsDTO> getTemplateWithFoods(int userId);
    
    // 템플릿 ID로 식품 이름 리스트 조회 (내부적으로 사용)
    List<String> getFoodNamesForTemplate(int templateId);
}
