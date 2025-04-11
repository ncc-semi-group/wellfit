package com.example.demo.dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("UserDto")
public class UserDto {
    private int id;                    // id
    private String email;              // 이메일
    private String password;           // 비밀번호 (해싱된 비밀번호)
    private String nickname;           // 닉네임
    private String profileImage;       // 프로필 사진
    private String gender;             // 성별
    private int age;                   // 나이
    private int height;                // 키
    private String activityLevel;      // 활동계수 (예: Sedentary, Lightly Active 등)
    private int currentWeight;         // 현재 체중
    private int goalWeight;            // 목표 체중
    private short cheatingDay;         // 치팅 데이
    private String type;               // 식단계획 유형 (예: custom, common, vegan 등)
    private short carbohydrate;        // 탄수화물
    private short protein;             // 단백질
    private short fat;                 // 지방
    private String createdAt;          // 생성시간 (timestamp)
    private String myIntro;            // 자기소개
}
