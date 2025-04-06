package com.example.demo.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class BoardController {

    @GetMapping("/feed/all")
    public String board(Model model) {
        model.addAttribute("showHeader", false);
        model.addAttribute("currentPage", "community");
        return "views/board/boardmain";
    }

    // 프로필 이미지 반환 API
    @GetMapping("/get-profile-image")
    @ResponseBody
    public Map<String, String> getProfileImage() {
        // 프로필 이미지 URL을 JSON 형태로 반환
        Map<String, String> response = new HashMap<>();
        response.put("profileImageUrl", "/images/프로필_이미지.svg");
        return response;
    }

    @GetMapping("/get-post-image")
    @ResponseBody
    public Map<String, String> getPostImage() {
        // 프로필 이미지 URL을 JSON 형태로 반환
        Map<String, String> response = new HashMap<>();
        response.put("postImageUrl", "/images/고릴라.png");
        return response;
    }
    
    
    
    
    
    @GetMapping("/feed/following")
    public String boardfollowing(Model model) {
        model.addAttribute("showHeader", false);
        model.addAttribute("currentPage", "community");
        return "views/board/boardfollowing";
    }

    @GetMapping("/feed/popular")
    public String boardmost(Model model) {
        model.addAttribute("currentPage", "community");
        return "views/board/boardmost";
    }
}
