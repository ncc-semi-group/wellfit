package com.example.demo.record.controller;

import com.example.demo.dto.record.FoodNutritionDto;
import com.example.demo.record.service.RecordService;
import com.example.demo.record.service.TemplateDetailService;
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
public class TemplateDetailController {
    
    private final TemplateDetailService templateDetailService;
    private final RecordService recordService;
    
    @GetMapping("/record/template_detail")
    public String templateDetail(Model model, HttpSession session,
                                 @RequestParam("template_id") int templateId,
                                 @RequestParam String name) {
        
        model.addAttribute("showHeader",false);
        model.addAttribute("showFooter",false);
        
        // 세션에서 날짜 가져오기
        Object sqlDateObj = session.getAttribute("sqlDate");
        if (sqlDateObj == null) {
            return "redirect:/loginpage"; // 로그인 페이지로 리다이렉트
        }
        java.sql.Date sqlDate = (java.sql.Date) sqlDateObj;
        
        // 템플릿 목록 가져오기
        List<FoodNutritionDto> templateItems = templateDetailService.getTemplateItems(templateId);
        // 모델에 데이터 추가
        model.addAttribute("templateItems", templateItems);
        model.addAttribute("name", name);
        
        
        return "views/record/template_detail";
    }
    
    @PostMapping("/api/record/add_template_food")
    @ResponseBody
    public ResponseEntity<?> addTemplateFood(HttpSession session,
                                             @RequestParam int templateId,
                                             @RequestParam int kcal) {
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 세션에서 식단 기록 ID 가져오기
        int foodRecordsId = (int) session.getAttribute("foodRecordsId");
        
        
        // 세션에서 날짜 가져오기
        java.sql.Date sqlDate = (java.sql.Date) session.getAttribute("sqlDate");
        
        // 치팅 여부 확인
        boolean isCheating = recordService.cheatingCheck(userId, sqlDate);
        if (isCheating) {
            return ResponseEntity.status(403).body("치팅데이로 설정한 날짜에 대해선 음식 추가/삭제가 불가능합니다.");
        }
        
        // 템플릿 음식 추가
        templateDetailService.addFoodRecordItems(templateId, foodRecordsId, kcal, userId, sqlDate);
        
        
        return ResponseEntity.ok().body("템플릿이 식단에 추가되었습니다. 이전 페이지로 이동합니다.");
    }
    
    
    @PostMapping("/api/record/delete_template")
    @ResponseBody
    public ResponseEntity<?> deleteTemplate(@RequestParam int templateId) {
        // 템플릿 삭제
        templateDetailService.deleteFoodTemplate(templateId);
        
        return ResponseEntity.ok().body("템플릿이 삭제되었습니다. 이전 페이지로 이동합니다.");
    }
}
