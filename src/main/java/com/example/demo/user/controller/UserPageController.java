package com.example.demo.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.badge.service.BadgeService;
import com.example.demo.badge.service.UserBadgeService;
import com.example.demo.board.service.BoardLikeService;
import com.example.demo.dto.user.BadgeDto;
import com.example.demo.dto.user.UserBadgeDto;
import com.example.demo.dto.user.UserDto;
import com.example.demo.user.service.UserPageService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserPageController {
    private final UserPageService userPageService;
    private final UserBadgeService userBadgeService;
    private final BadgeService badgeService;
    private final BoardLikeService boardLikeService;

    @GetMapping("/userpage/{userId}")
    public String userPage(@PathVariable("userId") int userId, Model model, HttpSession session) {
        // 현재 로그인한 사용자 확인
        Integer loginUserId = (Integer) session.getAttribute("userId");
        
        // 본인 프로필을 조회하려는 경우 마이페이지로 리다이렉트
        if (loginUserId != null && loginUserId == userId) {
            return "redirect:/mypage";
        }

        UserDto userDto = userPageService.getUserProfile(userId);
        model.addAttribute("user", userDto);
        
        return "views/userpage/userpage";
    }
    
    @GetMapping("/users")
    public String userList(Model model, HttpSession session) {
        Integer loginUserId = (Integer) session.getAttribute("userId");
        if (loginUserId == null) {
            return "redirect:/login";
        }
        
        List<UserDto> otherUsers = userPageService.getOtherUsers(loginUserId);
        model.addAttribute("users", otherUsers);
        
        return "views/userpage/userlist";
    }
    
    @GetMapping("/userpageBadge/{userId}")
    public String badgeList(@PathVariable("userId") int userId, Model model) {
        // 전체 뱃지 목록 가져오기
        List<BadgeDto> allBadges = badgeService.getAllBadges();
        
        // 해당 사용자의 뱃지 현황 가져오기
        List<UserBadgeDto> userBadges = userBadgeService.getSelectUserId(userId);
        
        // 사용자 정보도 함께 가져오기
        UserDto userDto = userPageService.getUserProfile(userId);
        
        // 뱃지 정보 합치기
        for (BadgeDto badge : allBadges) {
            // 사용자가 보유한 뱃지인지 확인
            UserBadgeDto userBadge = userBadges.stream()
                .filter(ub -> ub.getBadgeId() == badge.getId())
                .findFirst()
                .orElse(null);
            
            if (userBadge != null) {
                badge.setIsAchieved(userBadge.getIsAchieved());
                badge.setCondition_count(userBadge.getCondition_count());
            } else {
                badge.setIsAchieved(0);
                badge.setCondition_count(0);
            }
        }
        
        model.addAttribute("user", userDto);  // 사용자 정보도 뷰에 전달
        model.addAttribute("badgeList", allBadges);
        return "views/userpage/userpageBadge";
    }
    
    @GetMapping("/api/userExercises/{userId}")
    @ResponseBody
    public List<Map<String, Object>> getUserExercises(
            @PathVariable("userId") int userId,
            @RequestParam String start,
            @RequestParam String end) {
        
        // 운동 기록 서비스를 통해 해당 기간의 운동 데이터 조회
        return userPageService.getUserExercises(userId, start, end);
    }
}