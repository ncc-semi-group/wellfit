package com.example.demo.record.service;

import com.example.demo.dto.FoodNutritionDto;
import com.example.demo.dto.FoodRecordsDto;
import com.example.demo.dto.TemplateItemsDto;
import com.example.demo.record.mapper.FoodRecordMapper;
import com.example.demo.record.mapper.SearchMapper;
import com.example.demo.record.mapper.TemplateDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TemplateDetailService {
    
    private final TemplateDetailMapper templateDetailMapper;
    private final SearchMapper searchMapper;
    private final FoodRecordMapper foodRecordMapper;
    
    @Transactional
    public List<FoodNutritionDto> getTemplateItems(int templateId) {
        return templateDetailMapper.getTemplateItems(templateId);
    }
    
    // 템플릿 아이템들 식단 기록에 추가
    @Transactional
    public void addFoodRecordItems(int templateId, int foodRecordsId, int kcal, int userId, Date sqlDate) {
        // 템플릿 아이템들 가져오기
        List<TemplateItemsDto> templateItems = templateDetailMapper.getTemplateItemsOnly(templateId);
        
        
        // 템플릿 아이템들에 foodRecordsId 추가
        for (TemplateItemsDto item : templateItems) {
            item.setFoodRecordsId(foodRecordsId);
        }
        
        // 식단 기록에 추가
        templateDetailMapper.addFoodRecordItems(templateItems);
        
        // food_records 에 kcal 업데이트
        FoodRecordsDto foodRecords = new FoodRecordsDto();
        foodRecords.setId(foodRecordsId);
        foodRecords.setKcal(kcal);
        searchMapper.updateFoodRecordsKcal(foodRecords);
        
        
        // 통계 정보 업데이트
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("date", sqlDate);
        System.out.println("params = " + params);
        foodRecordMapper.updateDailyStatisticsFromFoodRecords(params);
    }
    
    // 템플릿 삭제
    @Transactional
    public void deleteFoodTemplate(int templateId) {
        // 템플릿 삭제
        templateDetailMapper.deleteFoodTemplates(templateId);
    }
}
