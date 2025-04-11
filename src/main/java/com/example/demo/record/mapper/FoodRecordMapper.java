package com.example.demo.record.mapper;

import com.example.demo.dto.record.FoodNutritionDto;
import com.example.demo.dto.record.TemplateItemsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FoodRecordMapper {
    
    List<FoodNutritionDto> getFoodRecordItems(int foodRecordsId);
    
    void updateDailyStatisticsFromFoodRecords(Map<String, Object> params);
    
    void deleteFoodRecordItems(int recordId);
    
    void addFoodTemplate(Map<String, Object> params);
    
    int getFoodTemplateId(Map<String, Object> params);
    
    List<TemplateItemsDto> getFoodRecordItemsOnly(int foodRecordsId);
    
    void addFoodTemplateItems(List<TemplateItemsDto> list);
    
}
