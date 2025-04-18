package com.example.demo.record.service;

import com.example.demo.dto.record.FoodNutritionDto;
import com.example.demo.dto.record.FoodRecordsDto;
import com.example.demo.dto.record.TemplateItemsDto;
import com.example.demo.record.mapper.FoodRecordMapper;
import com.example.demo.record.mapper.SearchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FoodRecordService {
    
    private final FoodRecordMapper foodRecordMapper;
    private final SearchMapper searchMapper;
    private final RecordService recordService;
    
    @Transactional
    public int getFoodRecordsId(int userId, String mealType, Date date) {
        FoodRecordsDto foodRecords = new FoodRecordsDto();
        foodRecords.setUserId(userId);
        foodRecords.setMealType(mealType);
        foodRecords.setDate(date);
        return searchMapper.getFoodRecordsId(foodRecords);
    }
    
    @Transactional
    public List<FoodNutritionDto> getFoodRecordItems(int foodRecordsId) {
        return foodRecordMapper.getFoodRecordItems(foodRecordsId);
    }
    
    // 식단기록에서 음식 삭제
    @Transactional
    public void deleteFoodRecordItems(int recordId, int userId, Date sqlDate, int kcal, int foodRecordsId) {
        // 음식 기록 삭제
        foodRecordMapper.deleteFoodRecordItems(recordId);
        
        // food_records 에 kcal 업데이트
        FoodRecordsDto foodRecords = new FoodRecordsDto();
        foodRecords.setId(foodRecordsId);
        // kcal은 음식을 삭제했으므로 -kcal로 업데이트
        foodRecords.setKcal(-kcal);
        searchMapper.updateFoodRecordsKcal(foodRecords);
        
        // 통계 정보 업데이트
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("date", sqlDate);
        System.out.println("params = " + params);
        foodRecordMapper.updateDailyStatisticsFromFoodRecords(params);
        
        // 성취 여부 검증
        recordService.achievedCheck(userId, sqlDate);
    }
    
    // 식단기록 템플릿 추가
    @Transactional
    public void addFoodTemplate(int userId, String name, int kcal, int foodRecordsId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("name", name);
        params.put("kcal", kcal);
        
        // 템플릿 추가
        foodRecordMapper.addFoodTemplate(params);
        
        // 템플릿 ID 가져오기
        int foodTemplateId = foodRecordMapper.getFoodTemplateId(params);
        
        // 식단 기록 아이템 가져오기
        List<TemplateItemsDto> items = foodRecordMapper.getFoodRecordItemsOnly(foodRecordsId);
        
        // 템플릿 아이템 추가
        for (TemplateItemsDto item : items) {
            item.setFoodTemplateId(foodTemplateId);
        }
        
        foodRecordMapper.addFoodTemplateItems(items);
    }
}
