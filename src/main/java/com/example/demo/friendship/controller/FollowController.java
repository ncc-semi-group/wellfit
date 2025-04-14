package com.example.demo.friendship.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.friendship.service.FollowService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @GetMapping("/followingpage")
    public String followingPage(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/loginpage";
        }

        List<UserDto> followingList = followService.getFollowingByUserId(userId);
        model.addAttribute("followingList", followingList);
        System.out.println("친구 목록: " + followingList);
        
        return "views/friendpage/followingpage";
    }
    
    @GetMapping("/followerpage")
    public String followerPage(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/loginpage";
        }

        List<UserDto> followerList = followService.getFollowerByUserId(userId);
        model.addAttribute("followerList", followerList);
        System.out.println("친구 목록: " + followerList);
        
        return "views/friendpage/followerpage";
    }

    @GetMapping("/api/followingpage/search")
    public ResponseEntity<List<UserDto>> searchFollowing(@RequestParam("nickname") String nickname, HttpSession session) {
    	Integer userId = (Integer) session.getAttribute("userId");
    	List<UserDto> userList = followService.searchFollowingByNickname(userId, nickname);
        return ResponseEntity.ok(userList);
    }
    
    @GetMapping("/api/followerpage/search")
    public ResponseEntity<List<UserDto>> searchFollower(@RequestParam("nickname") String nickname, HttpSession session) {
    	Integer userId = (Integer) session.getAttribute("userId");
    	List<UserDto> userList = followService.searchFollowerByNickname(userId, nickname);
        return ResponseEntity.ok(userList);
    }

}
