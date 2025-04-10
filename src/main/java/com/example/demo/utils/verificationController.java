package com.example.demo.utils;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// /src/main/java/com.example.demo/utils/verificationController.java
@RestController
public class verificationController {
    
    @PostMapping("/api/verification")
    public ResponseEntity<?> verify(HttpSession session) {
        // 세션에서 사용자 ID 가져오기
        Integer userId = (Integer) session.getAttribute("userId");
        System.out.println(userId);
        
        // 사용자 ID가 null인 경우
        if (userId == null) {
            return ResponseEntity.badRequest().body("사용자 ID가 세션에 없습니다.");
        } else {
            // 사용자 ID가 존재하는 경우
            return ResponseEntity.ok(true);
        }
    }
}
