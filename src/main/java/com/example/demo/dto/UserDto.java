<<<<<<< HEAD
package com.example.demo.dto;

import java.sql.Timestamp;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("UserDto")
public class UserDto {
	private int id;
	private String email;
	private String password;
	private String nickname;
	private String profileImage;
	private String gender;
	private int age;
	private int height;
	private String activityLevel;
	private int currentWeight;
	private int goalWeight;
	private int cheatingDay;
	private String type;
	private int carbohydrate;
	private int protein;
	private int fat;
	private Timestamp createdAt;
	
	// 마이페이지 추가 필드
	private int friendCount;
	private int badgeCount;
	private int achievementCount;
	private int postCount;
	private int commentCount;
	private String myIntro;
	private int userId1;
	private int userId2;
	
	// 좋아요 목록용 필드
	private String title;
}
=======
package com.example.demo.dto;


import java.sql.Timestamp;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("UserDto")
public class UserDto {

	public enum Gender {
		Male, Female
	}
	public enum ActivityLevel {
		Sedentary, Lightly_Active, Moderately_Active, Very_Active
	}
	public enum DietType {
		custom, common, health, quito, vegan
	}

	
	
	private int id; // 사용자 ID (자동 증가)
	private String email; // 이메일
	private String password; // 비밀번호 (해싱)
	private String nickname; // 닉네임
	private String profileImage; // 프로필 사진
	private Gender gender; // 성별 (male, female)
	private Integer age; // 나이
	private Integer height; // 키
	private ActivityLevel activityLevel; // 활동계수 (Sedentary, Lightly Active, Moderately Active, Very Active)
	private Integer currentWeight; // 현재 체중
	private Integer goalWeight; // 목표 체중
	private Short cheatingDay; // 치팅 데이 (숫자로 표현된 값)
	private DietType type; // 식단 계획 유형 (custom, common, health, quito, vegan)
	private Short carbohydrate; // 탄수화물
	private Short protein; // 단백질
	private Short fat; // 지방
	private Timestamp createdAt; // 생성시간 (타임스탬프)
	private String myIntro; // 자기소개

}
>>>>>>> refs/remotes/origin/bsh
