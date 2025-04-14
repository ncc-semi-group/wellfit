package com.example.demo.record.controller;

import com.example.demo.daily.service.DailyStatisticsService;
import com.example.demo.dto.record.FoodRecordsDto;
import com.example.demo.dto.statistics.DailyStatisticsDto;
import com.example.demo.dto.user.UserDto;
import com.example.demo.record.mapper.RecordMapper;
import com.example.demo.record.service.RecordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RecordController {
    
    private final RecordService recordService;
    private final DailyStatisticsService dailyStatisticsService;
    
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
        
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/loginpage"; // 로그인 페이지로 리다이렉트
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 식단 기록 정보
        List<FoodRecordsDto> foodRecords = recordService.getFoodRecords(userId, sqlDate);
        Map<String, FoodRecordsDto> foodRecordsMap = new HashMap<>();
        for (FoodRecordsDto foodRecord : foodRecords) {
            String mealType = foodRecord.getMealType();
            foodRecordsMap.put(mealType, foodRecord);
        }
        model.addAttribute("foodRecords", foodRecordsMap);
        
        // 유저 정보 서비스에서 가공하여 가져오기
        Map<String, Object> userInfoMap = recordService.getUserInfo(userId, sqlDate);
        
        // 유저 정보
        UserDto user = (UserDto) userInfoMap.get("user");
        model.addAttribute("user", user);
        
        // 세션에 유저 체중 정보 저장 (운동 기록에서 사용)
        session.setAttribute("userWeight", user.getCurrentWeight());
        
        // 일일 통계 정보
        DailyStatisticsDto dailyStatistics = dailyStatisticsService.getDailyStatistics(userId, sqlDate.toLocalDate());
        model.addAttribute("dailyStatistics", dailyStatistics);
        
        // 체중 정보
        float pastMonthWeight = (float) userInfoMap.get("pastMonthWeight");
        float pastWeekWeight = (float) userInfoMap.get("pastWeekWeight");
        model.addAttribute("pastMonthWeight", pastMonthWeight);
        model.addAttribute("pastWeekWeight", pastWeekWeight);
        
        return "views/record/record";
    }
    
    @PostMapping("/api/record/update_weight")
    public ResponseEntity<?> updateWeight(HttpSession session,
                                          @RequestParam float weight,
                                          @RequestParam float goalWeight) {
        
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 세션에서 날짜 가져오기
        Date sqlDate = (Date) session.getAttribute("sqlDate");
        
        // 체중 정보 업데이트
        recordService.updateUserWeight(userId, sqlDate, weight, goalWeight);
        
        return ResponseEntity.ok().body("체중 정보가 업데이트되었습니다.");
    }
    
    @PostMapping("/api/record/use_cheat_point")
    @ResponseBody
    public ResponseEntity<?> useCheatPoint(HttpSession session,
                                           @RequestParam int cheatingKcal,
                                           @RequestParam int cheatPoint) {
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 세션에서 날짜 가져오기
        Date sqlDate = (Date) session.getAttribute("sqlDate");
        
        // 유효성 검사
        if (cheatingKcal <= 0 || cheatPoint < 0) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
        
        // 치팅 포인트 사용
        recordService.useCheatPoint(userId, sqlDate, cheatingKcal, cheatPoint);
        
        return ResponseEntity.ok().body("치팅데이로 설정하였습니다.");
    }
    
}
