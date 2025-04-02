package data.dto;

import org.apache.ibatis.type.Alias;

@Alias("BoardLikeDto")
public class BoardLikeDto {
	private int id;
	private int userId;
	private int boardId;
}
