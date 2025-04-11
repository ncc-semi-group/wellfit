package com.example.demo.record.service;

import com.example.demo.dto.record.*;
import com.example.demo.record.elasticsearch.FoodDocument;
import com.example.demo.record.mapper.FoodRecordMapper;
import com.example.demo.record.mapper.SearchMapper;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    
    private final ElasticsearchOperations elasticsearchOperations;
    private final SearchMapper searchMapper;
    private final FoodRecordMapper foodRecordMapper;
    
    
    // 고급 검색 메소드 - ElasticsearchOperations 사용
    public List<FoodDocument> advancedSearch(String keyword, Pageable pageable) {
        // 공백 제거한 키워드 생성
        String keywordNoSpace = keyword.replaceAll("\\s+", "");
        
        // bool 쿼리 구성
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("name", keyword))
                .should(QueryBuilders.termQuery("name_no_space", keywordNoSpace))
                .should(QueryBuilders.matchQuery("manufacturer_name", keyword))
                .should(QueryBuilders.termQuery("manufacturer_no_space", keywordNoSpace));
        
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                // 1. 먼저 제조사명 유무로 정렬 (없는 것이 먼저 오도록)
                .withSort(SortBuilders.scriptSort(
                        new Script("doc.containsKey('manufacturer_name') && doc['manufacturer_no_space.keyword'].size() > 0 ? 1 : 0"),
                        ScriptSortBuilder.ScriptSortType.NUMBER))
                // 2. 그 다음 이름에 키워드가 맨 앞에 포함된 것 우선 정렬
                .withSort(SortBuilders.scriptSort(
                        new Script("doc['name_no_space.keyword'].value.indexOf('" + keyword + "') == 0 ? 0 : 1"),
                        ScriptSortBuilder.ScriptSortType.NUMBER))
                // 3. 그 다음 이름 길이로 정렬
                .withSort(SortBuilders.scriptSort(
                        new Script("doc['name_no_space.keyword'].value.length()"),
                        ScriptSortBuilder.ScriptSortType.NUMBER))
                .build();
        
        SearchHits<FoodDocument> searchHits = elasticsearchOperations.search(searchQuery, FoodDocument.class);
        
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
    
    
    /**
     * 주어진 키워드에 대해 고급 검색 조건과 일치하는 문서의 총 개수만 반환합니다.
     * 스코어링이나 페이징은 적용되지 않습니다.
     *
     * @param keyword 검색할 키워드
     * @return 검색 조건과 일치하는 총 문서 개수
     */
    public long countAdvancedSearch(String keyword) {
        // 1. 키워드 처리 (공백 제거)
        String keywordNoSpace = keyword.replaceAll("\\s+", "");
        
        // 2. 매칭 조건 쿼리 생성 (boolQuery만 사용)
        //    Function Score 로직은 개수만 셀 때는 불필요합니다.
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("name", keyword))
                .should(QueryBuilders.termQuery("name_no_space", keywordNoSpace))
                .should(QueryBuilders.matchQuery("manufacturer_name", keyword))
                .should(QueryBuilders.termQuery("manufacturer_no_space", keywordNoSpace));
        
        // 3. NativeSearchQuery 생성 (페이징 정보 없이 Query만 설정)
        NativeSearchQuery countQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .build();
        
        // 4. ElasticsearchOperations의 count 메소드 사용
        return elasticsearchOperations.count(countQuery, FoodDocument.class);
    }
    
    
    /**
     * 식단 기록 ID를 조회하거나 생성합니다.
     *
     * @param userId   사용자 ID
     * @param mealType 식사 유형
     * @param date     날짜
     * @return 식단 기록 ID
     */
    @Transactional
    public Integer getFoodRecordsId(int userId, String mealType, Date date) {
        // FoodRecordsDTO 객체 생성
        FoodRecordsDto foodRecords = new FoodRecordsDto();
        foodRecords.setUserId(userId);
        foodRecords.setMealType(mealType);
        foodRecords.setDate(date);
        
        // Integer로 받아서 null 처리 가능하게 함
        Integer foodRecordsId = searchMapper.getFoodRecordsId(foodRecords);
        
        // null이거나 0인 경우 새로 생성
        if (foodRecordsId == null || foodRecordsId == 0) {
            searchMapper.addFoodRecords(foodRecords);
            foodRecordsId = searchMapper.getFoodRecordsId(foodRecords);
            // 여전히 null인 경우 예외 처리
            if (foodRecordsId == null) {
                throw new RuntimeException("식단 기록 ID를 생성할 수 없습니다.");
            }
        }
        
        return foodRecordsId;
    }
    
    // 식단 기록 갯수 가져오기
    @Transactional
    public int getRecordItemsCount(int foodRecordsId) {
        return searchMapper.getRecordItemsCount(foodRecordsId);
    }
    
    // 식단 기록 추가
    @Transactional
    public void insertRecordItems(int foodRecordsId, int foodId, String foodType, int amount, int serving, int kcal, int userId, Date sqlDate) {
        RecordItemsDto recordItems = new RecordItemsDto();
        recordItems.setFoodRecordsId(foodRecordsId);
        recordItems.setFoodId(foodId);
        recordItems.setFoodType(foodType);
        recordItems.setAmount(amount);
        recordItems.setServing(serving);
        searchMapper.addRecordItems(recordItems);
        
        // food_records 에 kcal 업데이트
        FoodRecordsDto foodRecords = new FoodRecordsDto();
        foodRecords.setId(foodRecordsId);
        foodRecords.setKcal(kcal);
        searchMapper.updateFoodRecordsKcal(foodRecords);

        // 통계 반영
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("date", sqlDate);
        System.out.println("params = " + params);
        
        // 통계 정보가 없으면 추가
        int count = searchMapper.getStatisticsCount(params);
        if (count == 0) searchMapper.addDailyStatistics(params);
        
        // 통계 정보 업데이트
        foodRecordMapper.updateDailyStatisticsFromFoodRecords(params);
    }
    
    // 즐겨찾기 목록 가져오기
    @Transactional
    public List<FoodNutritionDto> getUserFoodFavorites(int userId) {
        return searchMapper.getUserFoodFavorites(userId);
    }
    
    // 즐겨찾기 여부 확인
    @Transactional
    public boolean isFoodFavorite (int userId, int foodId, String foodType) {
        FoodFavoritesDto foodFavorites = new FoodFavoritesDto();
        foodFavorites.setUserId(userId);
        foodFavorites.setFoodId(foodId);
        foodFavorites.setFoodType(foodType);
        
        int count = searchMapper.isFoodFavorite(foodFavorites);
        return count > 0;
    }
    
    // 즐겨찾기 추가 / 삭제
    @Transactional
    public void toggleFoodFavorite (int userId, int foodId, String foodType, boolean isFavorite) {
        FoodFavoritesDto foodFavorites = new FoodFavoritesDto();
        foodFavorites.setUserId(userId);
        foodFavorites.setFoodId(foodId);
        foodFavorites.setFoodType(foodType);
        
        if (isFavorite) searchMapper.deleteFoodFavorites(foodFavorites);
        else searchMapper.addFoodFavorites(foodFavorites);
    }
    
    // 직접 등록한 식품 정보 가져오기
    @Transactional
    public List<FoodNutritionDto> getIndividualFoodNutrition (int userId) {
        return searchMapper.getIndividualFoodNutrition(userId);
    }
    
    // 식단 템플릿 정보 가져오기
    @Transactional
    public List<TemplateWithFoodsDto> getTemplatesForUser(int userId) {
        return searchMapper.getTemplateWithFoods(userId);
    }
}
