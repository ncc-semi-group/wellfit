package com.example.demo.record.controller;

import com.example.demo.dto.record.FoodNutritionDto;
import com.example.demo.dto.record.TemplateWithFoodsDto;
import com.example.demo.record.elasticsearch.FoodDocument;
import com.example.demo.record.service.RecordService;
import com.example.demo.record.service.SearchService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {
    
    private final SearchService searchService;
    private final RecordService recordService;
    
    @GetMapping("/api/foods/search")
    @ResponseBody
    public ResponseEntity<List<FoodDocument>> advancedSearch(@RequestParam String keyword, @RequestParam int pageNo) {
        int pageSize = 20; // 페이지당 문서 수

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        
        List<FoodDocument> results = searchService.advancedSearch(keyword, pageable);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/api/foods/count")
    @ResponseBody
    public ResponseEntity<?> countAdvancedSearch(@RequestParam String keyword) {
        long totalCount = searchService.countAdvancedSearch(keyword);
        return ResponseEntity.ok(totalCount);
    }
    
    
    @GetMapping("/record/search")
    public String search(Model model, HttpSession session,
                         @RequestParam("meal_type") String mealType) {
        
        model.addAttribute("showHeader",false);
        model.addAttribute("showFooter",false);
        
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/loginpage"; // 로그인 페이지로 리다이렉트
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 세션에서 날짜 가져오기
        java.sql.Date sqlDate = (java.sql.Date) session.getAttribute("sqlDate");
        
        // food_records 테이블에서 식단 기록 정보 갯수 가져오기
        int foodRecordsId = searchService.getFoodRecordsId(userId, mealType, sqlDate);
        // 세션에 저장
        session.setAttribute("foodRecordsId", foodRecordsId);
        int foodRecordsCount = searchService.getRecordItemsCount(foodRecordsId);
        model.addAttribute("foodRecordsCount", foodRecordsCount);
        
        // 즐겨찾기 정보 가져오기
        List<FoodNutritionDto> favoriteList = searchService.getUserFoodFavorites(userId);
        model.addAttribute("favoriteList", favoriteList);
        
        // 템플릿 정보 가져오기
        List<TemplateWithFoodsDto> templateList = searchService.getTemplatesForUser(userId);
        model.addAttribute("templateList", templateList);
        
        // 직접 등록한 식단 정보 가져오기
        List<FoodNutritionDto> individualList = searchService.getIndividualFoodNutrition(userId);
        model.addAttribute("individualList", individualList);
        
        // 세션에 mealType 저장
        session.setAttribute("mealType", mealType);
        
        return "views/record/search";
    }
    
    @PostMapping("/api/record/add_item")
    @ResponseBody
    public ResponseEntity<?> addItem(HttpSession session,
                                     @RequestParam int foodId,
                                     @RequestParam String foodType,
                                     @RequestParam int amount,
                                     @RequestParam int serving,
                                     @RequestParam int kcal) {
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 세션에서 foodRecordsId 가져오기
        Integer foodRecordsId = (Integer) session.getAttribute("foodRecordsId");
        if (foodRecordsId == null) {
            return ResponseEntity.badRequest().body("식단 기록 ID가 세션에 없습니다.");
        }
        
        // 날짜 가져오기
        java.sql.Date sqlDate = (java.sql.Date) session.getAttribute("sqlDate");
        
        // 치팅 여부 확인
        boolean isCheating = recordService.cheatingCheck(userId, sqlDate);
        if (isCheating) {
            return ResponseEntity.status(403).body("치팅데이로 설정한 날짜에 대해선 음식 추가/삭제가 불가능합니다.");
        }
        
        // 식단 기록 항목 추가
        searchService.insertRecordItems(foodRecordsId, foodId, foodType, amount, serving, kcal, userId, sqlDate);
        return ResponseEntity.ok("식단 기록 항목이 추가되었습니다.");
    }
    
    @PostMapping("/api/record/get_items_count")
    @ResponseBody
    public ResponseEntity<?> getItemsCount(HttpSession session) {
        // 세션에서 foodRecordsId 가져오기
        Object foodRecordsIdObj = session.getAttribute("foodRecordsId");
        if (foodRecordsIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int foodRecordsId = Integer.parseInt(foodRecordsIdObj.toString());
        
        // 식단 기록 항목 갯수 가져오기
        int foodRecordsCount = searchService.getRecordItemsCount(foodRecordsId);
        return ResponseEntity.ok(foodRecordsCount);
    }
    
    @PostMapping("/api/record/is_favorite")
    @ResponseBody
    public ResponseEntity<?> isFavorite(HttpSession session,
                                        @RequestParam int foodId, @RequestParam String foodType) {
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 즐겨찾기 여부 확인
        boolean isFavorite = searchService.isFoodFavorite(userId, foodId, foodType);
        return ResponseEntity.ok(isFavorite);
    }
    
    @PostMapping("/api/record/favorite")
    @ResponseBody
    public ResponseEntity<?> addFavorite(HttpSession session,
                                         @RequestParam int foodId, @RequestParam String foodType,
                                         @RequestParam boolean isFavorite) {
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 즐겨찾기 추가 / 삭제
        searchService.toggleFoodFavorite(userId, foodId, foodType, isFavorite);
        
        return ResponseEntity.ok("즐겨찾기 상태가 변경되었습니다.");
    }
    
    
}
