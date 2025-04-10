package com.example.demo.record.service;

import com.example.demo.dto.FoodNutritionDTO;
import com.example.demo.dto.FoodRecordsDTO;
import com.example.demo.dto.TemplateItemsDTO;
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
    
    @Transactional
    public int getFoodRecordsId(int userId, String mealType, Date date) {
        FoodRecordsDTO foodRecords = new FoodRecordsDTO();
        foodRecords.setUserId(userId);
        foodRecords.setMealType(mealType);
        foodRecords.setDate(date);
        return searchMapper.getFoodRecordsId(foodRecords);
    }
    
    @Transactional
    public List<FoodNutritionDTO> getFoodRecordItems(int foodRecordsId) {
        return foodRecordMapper.getFoodRecordItems(foodRecordsId);
    }
    
    // 식단기록에서 음식 삭제
    @Transactional
    public void deleteFoodRecordItems(int recordId, int userId, Date sqlDate, int kcal, int foodRecordsId) {
        // 음식 기록 삭제
        foodRecordMapper.deleteFoodRecordItems(recordId);
        
        // food_records 에 kcal 업데이트
        FoodRecordsDTO foodRecords = new FoodRecordsDTO();
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
        List<TemplateItemsDTO> items = foodRecordMapper.getFoodRecordItemsOnly(foodRecordsId);
        
        // 템플릿 아이템 추가
        List<TemplateItemsDTO> templateItemsList = items.stream()
                .map(item -> {
                    TemplateItemsDTO templateItem = new TemplateItemsDTO();
                    templateItem.setFoodTemplateId(foodTemplateId);
                    templateItem.setFoodId(item.getFoodId());
                    templateItem.setFoodType(item.getFoodType());
                    templateItem.setAmount(item.getAmount());
                    templateItem.setServing(item.getServing());
                    return templateItem;
                })
                .toList();
        foodRecordMapper.addFoodTemplateItems(templateItemsList);
    }
}
