package com.example.demo.util;

import com.example.demo.dto.user.UserDto;

public class CalorieCalculator {
    
    // BMR 계산 (정수 반환)
    public static int calculateBMR(UserDto user) {
        float weight = user.getCurrentWeight(); // float
        int height = user.getHeight();          // cm
        int age = user.getAge();                // 세
        String gender = user.getGender();
        
        double bmr;
        if ("male".equalsIgnoreCase(gender)) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else if ("female".equalsIgnoreCase(gender)) {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        } else {
            throw new IllegalArgumentException("성별 값이 잘못되었습니다: " + gender);
        }
        
        return (int) Math.round(bmr);
    }
    
    // 활동계수 추출
    private static double getActivityFactor(String activityLevel) {
        switch (activityLevel) {
            case "Sedentary": return 1.2;
            case "Lightly Active": return 1.35;
            case "Moderately Active": return 1.55;
            case "Very Active": return 1.75;
            default: throw new IllegalArgumentException("알 수 없는 활동 수준: " + activityLevel);
        }
    }
    
    // AMR 계산 (정수 반환)
    public static int calculateAMR(UserDto user) {
        int bmr = calculateBMR(user);
        double factor = getActivityFactor(user.getActivityLevel());
        double amr = bmr * factor;
        return (int) Math.round(amr);
    }
    
    // 목표 섭취 칼로리 계산 (정수 반환)
    public static int calculateTargetCalories(UserDto user) {
        int amr = calculateAMR(user);
        float current = user.getCurrentWeight();
        float goal = user.getGoalWeight();
        
        float diff = Math.abs(current - goal);
        
        if (diff < 2) {
            return amr; // 유지
        } else if (current > goal) {
            return amr - 500; // 다이어트
        } else {
            return (int) Math.round(amr * 1.1); // 체중 증가
        }
    }
}
