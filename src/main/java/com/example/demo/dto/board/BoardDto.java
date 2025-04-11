package com.example.demo.dto.board;

import java.sql.Timestamp;
import java.util.List;

import com.example.demo.dto.user.UserDto;
import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("BoardDto")
public class BoardDto {

	private int id;
	private int userId;
	private Timestamp createdAt;
	private String content;
	private int likesCount;
	private int commentCount;
	private UserDto user;
	private List<BoardImageDto> images;
	private String nickname;
	

	// 이미지 관련 필드
	private int imageCount;
	private String thumbnailImage;
	private List<String> imageUrls;
}
