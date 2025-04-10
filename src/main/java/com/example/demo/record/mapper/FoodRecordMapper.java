package com.example.demo.record.mapper;

import com.example.demo.dto.FoodNutritionDTO;
import com.example.demo.dto.TemplateItemsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FoodRecordMapper {
    
    List<FoodNutritionDTO> getFoodRecordItems(int foodRecordsId);
    
    void updateDailyStatisticsFromFoodRecords(Map<String, Object> params);
    
    void deleteFoodRecordItems(int recordId);
    
    void addFoodTemplate(Map<String, Object> params);
    
    int getFoodTemplateId(Map<String, Object> params);
    
    List<TemplateItemsDTO> getFoodRecordItemsOnly(int foodRecordsId);
    
    void addFoodTemplateItems(List<TemplateItemsDTO> list);
    
}
