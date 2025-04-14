package com.example.demo.record.controller;

import com.example.demo.dto.record.FoodNutritionDto;
import com.example.demo.record.service.FoodRecordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
public class FoodRecordController {
    
    private final FoodRecordService foodRecordService;
    
    @GetMapping("/record/food_record")
    public String foodRecord(Model model, HttpSession session,
                             @RequestParam("meal_type") String mealType) {
        
        model.addAttribute("showHeader",false);
        model.addAttribute("showFooter",false);
        
        // 세션에서 날짜 가져오기
        java.sql.Date sqlDate = (java.sql.Date) session.getAttribute("sqlDate");
        int year = sqlDate.toLocalDate().getYear();
        int month = sqlDate.toLocalDate().getMonthValue();
        int day = sqlDate.toLocalDate().getDayOfMonth();
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("day", day);
        
        // 유저 ID 설정 (예시로 1 사용)
        int userId = 1; // 실제 사용자 ID로 변경해야 함
        
        // food_records_id 가져오기
        int foodRecordsId = foodRecordService.getFoodRecordsId(userId, mealType, sqlDate);
        // 세션에 저장
        session.setAttribute("foodRecordsId", foodRecordsId);
        
        // 식사 기록 조회
        List<FoodNutritionDto> foodRecordItems = foodRecordService.getFoodRecordItems(foodRecordsId);
        
        // 모델에 데이터 추가
        model.addAttribute("foodRecordItems", foodRecordItems);
        
        // mealType 지정
        String transMealType = switch (mealType) {
            case "breakfast" -> "아침";
            case "lunch" -> "점심";
            case "dinner" -> "저녁";
            case "snack" -> "간식";
            default -> "";
        };
        model.addAttribute("mealType", transMealType);
        
        
        return "views/record/food_record";
    }
    
    @PostMapping("/api/record/delete_item")
    @ResponseBody
    public ResponseEntity<?> deleteItem(HttpSession session,
                                        @RequestParam int recordId,
                                        @RequestParam int kcal) {
        // 유저 ID 설정 (예시로 1 사용)
        int userId = 1; // 실제 사용자 ID로 변경해야 함
        
        // 세션에서 날짜, 식단기록id 가져오기
        java.sql.Date sqlDate = (java.sql.Date) session.getAttribute("sqlDate");
        int foodRecordsId = (Integer) session.getAttribute("foodRecordsId");
        
        // 음식 기록 삭제
        foodRecordService.deleteFoodRecordItems(recordId, userId, sqlDate, kcal, foodRecordsId);
        
        return ResponseEntity.ok("기록이 삭제되었습니다.");
    }
    
    @PostMapping("/api/record/add_template")
    @ResponseBody
    public ResponseEntity<?> addTemplate(HttpSession session,
                                         @RequestParam String templateName,
                                         @RequestParam int kcal) {
        
        // 유저 ID 설정 (예시로 1 사용)
        int userId = 1; // 실제 사용자 ID로 변경해야 함
        
        // food_records_id 가져오기
        int foodRecordsId = (Integer) session.getAttribute("foodRecordsId");
        
        // 템플릿 추가
        foodRecordService.addFoodTemplate(userId, templateName, kcal, foodRecordsId);
        

        
        return ResponseEntity.ok("템플릿이 추가되었습니다.");
    }
    
    
}
