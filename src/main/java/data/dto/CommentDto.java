package data.dto;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("CommentDto")
public class CommentDto {
	private int id;
	private int boardId;
	private int userId;
	private String comment;
	private int parentId;
	private Timestamp createdAt; //댓글 등록 날짜
	
	private List<HashtagDto> hashtags;
	private String nickname;
}
