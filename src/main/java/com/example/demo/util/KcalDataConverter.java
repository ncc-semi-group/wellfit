package com.example.demo.util;

import com.example.demo.dto.user.UserDto;

import java.util.Map;

public class KcalDataConverter {
    
    public static Map<String, Object> getKcalData(UserDto user) {
        
        // 기초대사량 계산
        int bmr;
        if (user.getGender().equals("male")) {
            bmr = (int) (10 * user.getCurrentWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5);
        } else if (user.getGender().equals("female")) {
            bmr = (int) (10 * user.getCurrentWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161);
        } else {
            throw new IllegalArgumentException("Invalid gender: " + user.getGender());
        }
        
        // 활동 대사량 계산
        int amr;
        switch (user.getActivityLevel()) {
            case "Sedentary" -> amr = (int) (bmr * 1.2);
            case "Lightly Active" -> amr = (int) (bmr * 1.375);
            case "Moderately Active" -> amr = (int) (bmr * 1.55);
            case "Very Active" -> amr = (int) (bmr * 1.725);
            default -> throw new IllegalArgumentException("Invalid activity level: " + user.getActivityLevel());
        }
        
        // 목표 체중에 도달하기 위한 칼로리 계산
        int targetKcal;
        String userTarget;
        float currentWeight = user.getCurrentWeight();
        float targetWeight = user.getGoalWeight();
        if (Math.abs(currentWeight - targetWeight) < 1) {
            targetKcal = amr;
            userTarget = "maintain";
        }
        else if (currentWeight > targetWeight) {
            targetKcal = amr - 500;
            userTarget = "diet";
        }
        else {
            targetKcal = (int) (amr * 1.1);
            userTarget = "gain";
        }
        
        // 결과를 Map에 저장
        return Map.of(
                "bmr", bmr,
                "amr", amr,
                "targetKcal", targetKcal,
                "userTarget", userTarget
        );
    }
}
