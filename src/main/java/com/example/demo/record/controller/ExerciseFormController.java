package com.example.demo.record.controller;

import com.example.demo.dto.record.ExerciseData;
import com.example.demo.dto.record.ExerciseRecordDto;
import com.example.demo.dto.user.UserDto;
import com.example.demo.record.mapper.RecordMapper;
import com.example.demo.record.service.ExerciseFormService;
import com.example.demo.record.service.RecordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ExerciseFormController {
    
    private final ExerciseFormService exerciseFormService;
    private final RecordService recordService;
    
    @GetMapping("/record/exercise_form")
    public String addForm(Model model, HttpSession session) {
        model.addAttribute("showHeader",false);
        model.addAttribute("showFooter",false);
        
        // 운동 정보 가져오기
        List<ExerciseData> exerciseList = new ArrayList<>();
        exerciseList.add(new ExerciseData("걷기", 3.5f));
        exerciseList.add(new ExerciseData("달리기", 9.0f));
        exerciseList.add(new ExerciseData("등산", 8.0f));
        exerciseList.add(new ExerciseData("자전거", 8.5f));
        exerciseList.add(new ExerciseData("줄넘기", 10.0f));
        exerciseList.add(new ExerciseData("헬스", 5.0f));
        exerciseList.add(new ExerciseData("축구", 8.5f));
        exerciseList.add(new ExerciseData("농구", 7.0f));
        exerciseList.add(new ExerciseData("배드민턴", 6.5f));
        exerciseList.add(new ExerciseData("수영", 8.0f));
        exerciseList.add(new ExerciseData("자유 운동", 0.0f));
        
        // 모델에 운동 정보 추가
        model.addAttribute("exerciseList", exerciseList);
        
        // 세션에서 체중 가져오기
        Object userWeightObj = session.getAttribute("userWeight");
        if (userWeightObj == null) {
            return "redirect:/record"; // 기록 페이지로 리다이렉트
        }
        float userWeight = Float.parseFloat(userWeightObj.toString());
        model.addAttribute("userWeight", userWeight);
     
        return "views/record/exercise_form";
    }
    
    @PostMapping("/api/record/add_exercise")
    @ResponseBody
    public ResponseEntity<?> addExerciseRecord(HttpSession session,
                                               @RequestBody ExerciseRecordDto exerciseRecord) {
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 세션에서 날짜 가져오기
        java.sql.Date sqlDate = (java.sql.Date) session.getAttribute("sqlDate");
        
        // 치팅 여부 확인
        boolean isCheating = recordService.cheatingCheck(userId, sqlDate);
        if (isCheating) {
            return ResponseEntity.status(403).body("치팅데이로 설정한 날짜에 대해선 운동 추가/삭제가 불가능합니다.");
        }
        
        // 운동 기록 추가
        exerciseRecord.setUserId(userId);
        exerciseRecord.setDate(sqlDate);
        exerciseFormService.addExerciseRecord(exerciseRecord);
        
        return ResponseEntity.ok().body("운동 기록이 추가되었습니다.");
    }
    
    @PostMapping("/api/record/exercise_count")
    @ResponseBody
    public ResponseEntity<?> getExerciseCount(HttpSession session) {
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 세션에서 날짜 가져오기
        java.sql.Date sqlDate = (java.sql.Date) session.getAttribute("sqlDate");
        
        // 운동 기록 개수 조회
        int exerciseCount = exerciseFormService.getExerciseRecordCount(userId, sqlDate);
        
        
        return ResponseEntity.ok(exerciseCount);
    }
    
}
