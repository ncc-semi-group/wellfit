package com.example.demo.record.controller;

import com.example.demo.dto.record.FoodNutritionDto;
import com.example.demo.record.service.AddFormService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;

@Controller
@RequiredArgsConstructor
public class AddFormController {
    
    private final AddFormService addFormService;
    
    @GetMapping("/record/add_form")
    public String addForm(Model model,
                          @RequestParam String type,
                          @RequestParam(name = "food_id", required = false) Integer foodId) {
        model.addAttribute("showHeader",false);
        model.addAttribute("showFooter",false);
        
        // type에 따라 다른 뷰를 반환
        model.addAttribute("type", type);
        if (type.equals("edit")) {
            FoodNutritionDto foodNutrition = addFormService.getFoodNutritionById(foodId);
            float weight = foodNutrition.getWeight();
            int servingSize = foodNutrition.getServingSize();
            float combiner;
            if (Math.floor(weight) == servingSize) {
                servingSize = 0;
                foodNutrition.setServingSize(servingSize);
            }
            combiner = servingSize == 0 ? weight / 100 : (float) servingSize / 100;
            
            // 영양소 포맷팅
            DecimalFormat df = new DecimalFormat("#.##");
            df.setDecimalSeparatorAlwaysShown(false);
            foodNutrition.setKcal((int) (foodNutrition.getKcal() * combiner));
            foodNutrition.setCarbohydrate(Float.parseFloat(df.format(foodNutrition.getCarbohydrate() * combiner)));
            foodNutrition.setSugar(Float.parseFloat(df.format(foodNutrition.getSugar() * combiner)));
            foodNutrition.setProtein(Float.parseFloat(df.format(foodNutrition.getProtein() * combiner)));
            foodNutrition.setFat(Float.parseFloat(df.format(foodNutrition.getFat() * combiner)));
            foodNutrition.setSaturatedFat(Float.parseFloat(df.format(foodNutrition.getSaturatedFat() * combiner)));
            foodNutrition.setTransFat(Float.parseFloat(df.format(foodNutrition.getTransFat() * combiner)));
            foodNutrition.setCholesterol(Float.parseFloat(df.format(foodNutrition.getCholesterol() * combiner)));
            foodNutrition.setNatrium((int) (foodNutrition.getNatrium() * combiner));
            
            model.addAttribute("combiner", combiner);
            model.addAttribute("food", foodNutrition);
        }
        
        return "views/record/add_form";
    }
    
    @PostMapping("/api/record/add_food")
    @ResponseBody
    public ResponseEntity<?> addFoodNutrition(HttpSession session,
                                              @RequestBody FoodNutritionDto foodNutrition) {
        
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 유저 id 추가
        foodNutrition.setUserId(userId);
        
        // insert
        addFormService.addFoodNutrition(foodNutrition);
        
        // 성공 응답
        return ResponseEntity.ok().body("성공적으로 추가되었습니다.");
    }
    
    @PostMapping("/api/record/update_food")
    @ResponseBody
    public ResponseEntity<?> updateFoodNutrition(HttpSession session,
                                                  @RequestBody FoodNutritionDto foodNutrition) {
        
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 유저 id 추가
        foodNutrition.setUserId(userId);
        
        // update
        addFormService.updateFoodNutrition(foodNutrition);
        
        // 성공 응답
        return ResponseEntity.ok().body("성공적으로 수정되었습니다.");
    }
    
    @PostMapping("/api/record/delete_food")
    @ResponseBody
    public ResponseEntity<?> deleteFoodNutrition(HttpSession session,
                                                  @RequestParam int foodId) {
        
        // 유저 ID 설정 및 검증
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        }
        int userId = Integer.parseInt(userIdObj.toString());
        
        // 템플릿에서 사용중인 foodId인지 검증
        boolean exists = addFormService.checkFoodItemExists(foodId, userId);
        if (exists) {
            return ResponseEntity.status(400).body("템플릿에 등록한 음식은 삭제할 수 없습니다.");
        } else {
            // delete
            addFormService.deleteFoodNutrition(foodId);
            
            // 성공 응답
            return ResponseEntity.ok().body("삭제되었습니다.");
        }
    }

}
