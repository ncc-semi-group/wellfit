package com.example.demo.main.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.board.service.BoardService;
import com.example.demo.dto.board.BoardDto;
import com.example.demo.dto.user.UserDto;
import com.example.demo.record.service.ExerciseDetailService;
import com.example.demo.record.service.RecordService;
import com.example.demo.user.service.UserService;
import com.example.demo.util.CalorieCalculator;
import com.example.demo.util.CalorieGraphGenerator;  // CalorieGraphGenerator 추가

@Controller
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;
    private final BoardService boardService;
    private final RecordService recordService;
    private final ExerciseDetailService exerciseDetailService;
    @Autowired
    private CalorieGraphGenerator calorieGraphGenerator;
    
    @GetMapping("/mainpage")
    public String searchFollower(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        
        if (userId == null) {
            System.out.println("세션에서 userId가 없습니다. 로그인 페이지로 리다이렉트");
            return "redirect:/loginpage";
        }
        UserDto user = userService.getUserById(userId);
        
        List<BoardDto> boardList = boardService.getBoardPreview();
        model.addAttribute("boardList", boardList);
        
        // targetCalories를 model에 추가
        int targetCalories = CalorieCalculator.calculateTargetCalories(user);

        // targetCalories를 CalorieGraphGenerator에 전달
        calorieGraphGenerator.generateGraph(userId, targetCalories); // 사용자 ID와 targetCalories 전달

        model.addAttribute("targetCalories", targetCalories);
        model.addAttribute("AMR", CalorieCalculator.calculateAMR(user));
        model.addAttribute("BMR", CalorieCalculator.calculateBMR(user));
        model.addAttribute("user", user);

        Date date = Date.valueOf("2025-04-13");
        Map<String, String> todayFoodRecord = recordService.getTodayFoodRecord(userId, date);
        model.addAttribute("kcalMap", todayFoodRecord);

        date = Date.valueOf("2025-04-11");
        int burnedKcalByExercise = exerciseDetailService.getTotalBurned(userId, date);
        model.addAttribute("burnedKcalByExercise", burnedKcalByExercise);
        model.addAttribute("leftKcal", targetCalories - Integer.parseInt(todayFoodRecord.get("total").replace("Kcal", "")) + burnedKcalByExercise);

        return "views/mainpage/mainpage";
    }
}
