package com.example.demo.dto;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("BoardDto")
public class BoardDto {

	private int id;
	private int userId;
	private Timestamp createdAt;
	private String content;
	private int followerCount;
	private int likesCount;
	private int commentCount;
	private boolean likedByCurrentUser;
    private boolean followedByCurrentUser;
	private UserDto user;
	private List<BoardImageDto> images;
	private String nickname;	
    private List<HashtagDto> hashtags;
	// 이미지 관련 필드
	private int imageCount;
	private String thumbnailImage;
	private List<String> imageUrls;
}
