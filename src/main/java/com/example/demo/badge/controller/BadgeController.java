package com.example.demo.badge.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.badge.service.UserBadgeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BadgeController {
    private final UserBadgeService userBadgeService;

    // 1. 첫 글쓰기 뱃지
    @PostMapping("/api/badge/first-post/update")
    public String updateFirstPostBadge(@RequestParam("userId") int userId) {
        try {
            userBadgeService.manualUpdatePostBadge(userId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 2. 구르미 뱃지
    @PostMapping("/api/badge/login/update")
    public String updateLoginBadge(
        @RequestParam("userId") int userId,
        @RequestParam("isAttendanceChecked") boolean isAttendanceChecked
    ) {
        try {
            userBadgeService.manualUpdateLoginBadge(userId, isAttendanceChecked);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 3. 파워 목표러 뱃지
    @PostMapping("/api/badge/goal-streak/update")
    public String updateGoalStreakBadge(
        @RequestParam("userId") int userId,
        @RequestParam("isAttendanceChecked") boolean isAttendanceChecked
    ) {
        try {
            userBadgeService.manualUpdateGoalStreakBadge(userId, isAttendanceChecked);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 4. 댓글 마스터 뱃지
    @PostMapping("/api/badge/comment/update")
    public String updateCommentBadge(@RequestParam("userId") int userId) {
        try {
            userBadgeService.manualUpdateCommentBadge(userId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 5. 기록 마스터 뱃지
    @PostMapping("/api/badge/record/update")
    public String updateRecordBadge(
        @RequestParam("userId") int userId,
        @RequestParam("hasRecorded") boolean hasRecorded
    ) {
        try {
            userBadgeService.manualUpdateRecordBadge(userId, hasRecorded);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 6. 나는야 금주왕 뱃지
    @PostMapping("/api/badge/alcohol-free/update")
    public String updateAlcoholFreeBadge(
        @RequestParam("userId") int userId,
        @RequestParam("hasNoAlcohol") boolean hasNoAlcohol
    ) {
        try {
            userBadgeService.manualUpdateAlcoholFreeBadge(userId, hasNoAlcohol);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 7. 좋아요 중독자 뱃지
    @PostMapping("/api/badge/like/update")
    public String updateLikeBadge(@RequestParam("userId") int userId) {
        try {
            userBadgeService.manualUpdateLikeBadge(userId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 8. 건강식단 소유자 뱃지
    @PostMapping("/api/badge/healthy-meal/update")
    public String updateHealthyMealBadge(
        @RequestParam("userId") int userId,
        @RequestParam("isHealthyMeal") boolean isHealthyMeal
    ) {
        try {
            userBadgeService.manualUpdateHealthyMealBadge(userId, isHealthyMeal);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 9. 파워 인싸 뱃지
    @PostMapping("/api/badge/friend/update")
    public String updateFriendBadge(@RequestParam("userId") int userId) {
        try {
            userBadgeService.manualUpdateFriendBadge(userId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 10. 식단 거장자 뱃지
    @PostMapping("/api/badge/meal-variety/update")
    public String updateMealVarietyBadge(@RequestParam("userId") int userId) {
        try {
            userBadgeService.manualUpdateMealVarietyBadge(userId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 11. 글작성 마스터 뱃지
    @PostMapping("/api/badge/post-count/update")
    public String updatePostCountBadge(@RequestParam("userId") int userId) {
        try {
            userBadgeService.manualUpdatePostCountBadge(userId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 12. 만보왕 뱃지
    @PostMapping("/api/badge/walking/update")
    public String updateWalkingBadge(
        @RequestParam("userId") int userId,
        @RequestParam("hasWalked10000") boolean hasWalked10000
    ) {
        try {
            userBadgeService.manualUpdateWalkingBadge(userId, hasWalked10000);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
} 