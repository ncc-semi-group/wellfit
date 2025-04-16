package com.example.demo.main.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.board.service.BoardImageService;
import com.example.demo.board.service.BoardService;
import com.example.demo.dto.board.BoardDto;
import com.example.demo.dto.board.BoardImageDto;
import com.example.demo.dto.user.UserDto;
import com.example.demo.record.service.ExerciseDetailService;
import com.example.demo.record.service.RecordService;
import com.example.demo.user.service.UserService;
import com.example.demo.util.CalorieCalculator;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;
    private final BoardService boardService;
    private final BoardImageService boardImageService;
    private final RecordService recordService;
    private final ExerciseDetailService exerciseDetailService;
    
    @GetMapping("/mainpage")
    public String searchFollower(HttpSession session, Model model) {
        model.addAttribute("showLogo", true);
        model.addAttribute("currentPage", "home");

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            System.out.println("세션에서 userId가 없습니다. 로그인 페이지로 리다이렉트");
            return "redirect:/loginpage";
        }

        UserDto user = userService.getUserById(userId);

        String userProfileImage = user.getProfileImage();
        model.addAttribute("userProfileImage", userProfileImage);

        int targetCalories = CalorieCalculator.calculateTargetCalories(user);

        model.addAttribute("targetCalories", targetCalories);
        model.addAttribute("AMR", CalorieCalculator.calculateAMR(user));
        model.addAttribute("BMR", CalorieCalculator.calculateBMR(user));
        model.addAttribute("user", user);

        LocalDate today = LocalDate.now();
        Date todaySqlDate = Date.valueOf(today);
        Map<String, String> todayFoodRecord = recordService.getTodayFoodRecord(userId, todaySqlDate);
        model.addAttribute("kcalMap", todayFoodRecord);

        int burnedKcalByExercise = exerciseDetailService.getTotalBurned(userId, todaySqlDate);
        model.addAttribute("burnedKcalByExercise", burnedKcalByExercise);

        int totalIntake = 0;
        try {
            totalIntake = Integer.parseInt(todayFoodRecord.get("total").replace("Kcal", "").trim());
        } catch (Exception e) {
            System.err.println("칼로리 파싱 에러: " + e.getMessage());
        }

        model.addAttribute("leftKcal", targetCalories - totalIntake + burnedKcalByExercise);

        List<BoardDto> posts = boardService.getBoardPreview();
        System.out.println(posts);

        model.addAttribute("posts", posts);

        return "views/mainpage/mainpage";
    }
    
    
    
    @GetMapping("/")
    public String redirectToMain() {
        return "redirect:/mainpage";
    }
}