package com.example.demo.record.controller;

import com.example.demo.dto.record.ExerciseRecordDto;
import com.example.demo.record.service.ExerciseDetailService;
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
public class ExerciseDetailController {
    
    private final ExerciseDetailService exerciseDetailService;
    
    @GetMapping("/record/exercise_detail")
    public String exerciseDetail(Model model, HttpSession session) {
        model.addAttribute("showHeader",false);
        model.addAttribute("showFooter",false);
        
        // userId 설정 (예시로 1 사용)
        int userId = 1; // 실제 사용자 ID로 변경해야 함
        
        // 세션에서 날짜 가져오기
        java.sql.Date sqlDate = (java.sql.Date) session.getAttribute("sqlDate");
        int year = sqlDate.toLocalDate().getYear();
        int month = sqlDate.toLocalDate().getMonthValue();
        int day = sqlDate.toLocalDate().getDayOfMonth();
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("day", day);
        
        // 운동 기록 조회
        List<ExerciseRecordDto> exerciseRecordItems = exerciseDetailService.getExerciseRecords(userId, sqlDate);
        
        // 모델에 데이터 추가
        model.addAttribute("exerciseRecordItems", exerciseRecordItems);
        
        return "views/record/exercise_detail";
    }
    
    @PostMapping("/api/record/delete_exercise")
    @ResponseBody
    public ResponseEntity<?> deleteExerciseRecord(HttpSession session,
                                               @RequestParam int exerciseId,
                                               @RequestParam int burnedKcal,
                                               @RequestParam int exerciseTime) {
        // 유저 ID 설정 (예시로 1 사용)
        int userId = 1; // 실제 사용자 ID로 변경해야 함
        System.out.println("exerciseId: " + exerciseId);
        // 세션에서 날짜 가져오기
        java.sql.Date sqlDate = (java.sql.Date) session.getAttribute("sqlDate");
        
        // 운동 기록 삭제
        exerciseDetailService.deleteExerciseRecord(exerciseId, burnedKcal, exerciseTime, userId, sqlDate);
        
        return ResponseEntity.ok("운동 기록이 삭제되었습니다.");
    }
    
}
