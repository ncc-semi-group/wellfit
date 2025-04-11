package com.example.demo.dto;

import org.apache.ibatis.type.Alias;
import lombok.Data;

@Data
@Alias("BadgeDto")
public class BadgeDto {
	private int id;
	private String badgeName;
	private String badgeContents;
	private String badgeImages;
	private String conditionType;
	private int conditionValue;
	
	// 사용자 뱃지 상태 관련 필드
	private int isAchieved;
	private int condition_count;
}
