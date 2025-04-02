package data.dto;

import java.sql.Timestamp;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("UserBadgeDto")
public class UserBadgeDto {
	private int id;
	private int userId;
	private int badgeId;
	private Timestamp achievedDate;
	private int isAchieved;
	private int condition_count;
	private int userBadgeId;
}
