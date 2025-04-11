package com.example.demo.record.controller;

import com.example.demo.dto.FoodRecordsDto;
import com.example.demo.record.service.RecordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RecordController {
    
    private final RecordService recordService;
    
    @GetMapping("/record")
    public String history(Model model, HttpSession session,
                          @RequestParam(required = false) Integer year,
                          @RequestParam(required = false) Integer month,
                          @RequestParam(required = false) Integer day) {
        model.addAttribute("currentPage", "record");
        model.addAttribute("pageTitle", "기록");
        
        // 날짜 설정
        if ( year == null || month == null || day == null) {
            LocalDate localDate = LocalDate.now();
            year = localDate.getYear();
            month = localDate.getMonthValue();
            day = localDate.getDayOfMonth();
        }
        
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("day", day);
        
        // 날짜 포맷
        String dateFormat = String.format("%04d-%02d-%02d", year, month, day);
        java.sql.Date sqlDate = java.sql.Date.valueOf(dateFormat);
        
        // 세션에 날짜 저장
        session.setAttribute("sqlDate", sqlDate);
        
        // 유저 ID 설정 (예시로 1 사용)
        int userId = 1; // 실제 사용자 ID로 변경해야 함
        
        // 식단 기록 정보
        List<FoodRecordsDto> foodRecords = recordService.getFoodRecords(userId, sqlDate);
        Map<String, FoodRecordsDto> foodRecordsMap = new HashMap<>();
        for (FoodRecordsDto foodRecord : foodRecords) {
            String mealType = foodRecord.getMealType();
            foodRecordsMap.put(mealType, foodRecord);
        }
        model.addAttribute("foodRecords", foodRecordsMap);
        return "views/record/record";
    }
    
}
