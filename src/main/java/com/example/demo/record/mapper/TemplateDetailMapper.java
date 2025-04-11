package com.example.demo.record.mapper;

import com.example.demo.dto.FoodNutritionDto;
import com.example.demo.dto.TemplateItemsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TemplateDetailMapper {
    
    List<FoodNutritionDto> getTemplateItems(int templateId);
    
    List<TemplateItemsDto> getTemplateItemsOnly(int templateId);
    
    void addFoodRecordItems(List<TemplateItemsDto> list);
    
    void deleteFoodTemplates(int templateId);
}
