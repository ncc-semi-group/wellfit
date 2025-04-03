package data.dto;

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