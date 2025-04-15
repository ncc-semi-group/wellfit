package com.example.demo.record.controller;

import com.example.demo.dto.user.UserDto;
import com.example.demo.record.mapper.RecordMapper;
import com.example.demo.record.service.StatisticsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    private final RecordMapper recordMapper;
    
    @GetMapping("/record/statistics")
    public String statistics(HttpSession session, Model model) {
        model.addAttribute("currentPage", "record");
        model.addAttribute("pageTitle", "통계");
        
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/login";
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 유저 정보 조회
        UserDto user = recordMapper.getUserById(userId);
        model.addAttribute("user", user);
        
        // 유저 프사 조회
        String userProfileImage = user.getProfileImage();
        model.addAttribute("userProfileImage", userProfileImage);
        
        return "views/record/statistics";
    }
    
    @PostMapping("/api/record/getStatistics")
    @ResponseBody
    public ResponseEntity<?> getStatistics(HttpSession session) throws JsonProcessingException {
        
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 통계 데이터 조회
        Map<String, Object> result = statisticsService.getUserStatistics(userId);
        
        // 통계 데이터 포맷팅
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writeValueAsString(result);
        
        return ResponseEntity.ok(jsonResult);
    }
}
