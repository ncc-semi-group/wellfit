package com.example.demo.friendship.controller;


import com.example.demo.dto.user.UserDto;
import com.example.demo.friendship.service.FollowService;
import com.example.demo.user.service.UserPageService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserPageService userPageService;

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
    
    @GetMapping("/userpage/following/{userId}")
    public String userFollowingList(@PathVariable("userId") int userId, Model model) {
        List<UserDto> followingList = followService.getFollowingByUserId(userId);
        UserDto userProfile = userPageService.getUserProfile(userId);
        model.addAttribute("followingList", followingList);
        model.addAttribute("user", userProfile);
        return "views/friendpage/followingpage";
    }

    @GetMapping("/userpage/follower/{userId}")
    public String userFollowerList(@PathVariable("userId") int userId, Model model) {
        List<UserDto> followerList = followService.getFollowerByUserId(userId);
        UserDto userProfile = userPageService.getUserProfile(userId);
        model.addAttribute("followerList", followerList);
        model.addAttribute("user", userProfile);
        return "views/friendpage/followerpage";
    }

}
