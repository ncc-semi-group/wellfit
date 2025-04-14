package com.example.demo.record.mapper;

import com.example.demo.dto.record.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SearchMapper {

    Integer getFoodRecordsId (FoodRecordsDto foodRecords);
    
    void addFoodRecords (FoodRecordsDto foodRecords);
    
    int getRecordItemsCount (int foodRecordsId);
    
    void addRecordItems (RecordItemsDto recordItems);
    
    List<FoodNutritionDto> getUserFoodFavorites (int userId);
    
    int isFoodFavorite (FoodFavoritesDto foodFavorites);
    
    void addFoodFavorites (FoodFavoritesDto foodFavorites);
    
    void deleteFoodFavorites (FoodFavoritesDto foodFavorites);
    
    List<FoodNutritionDto> getIndividualFoodNutrition (int userId);
    
    void updateFoodRecordsKcal (FoodRecordsDto foodRecords);
    
    int getStatisticsCount (Map<String, Object> params);
    
    void addDailyStatistics (Map<String, Object> params);
    
    // 사용자 ID로 템플릿 목록과 각 템플릿의 식품 이름 리스트 조회
    List<TemplateWithFoodsDto> getTemplateWithFoods(int userId);
    
    // 템플릿 ID로 식품 이름 리스트 조회 (내부적으로 사용)
    List<String> getFoodNamesForTemplate(int templateId);
}
