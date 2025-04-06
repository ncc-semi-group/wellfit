<<<<<<< HEAD
package com.example.demo.dto;

import java.sql.Timestamp;
import java.util.List;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("BoardDto")
public class BoardDto {
	private int id;
	private int userId;
	private String content;
	private Timestamp createdAt;
	private String nickname;
	
	// 해시태그 목록
	private List<HashtagDto> hashtags;
	
	// 이미지 관련 필드
	private int imageCount;
	private String thumbnailImage;
	private List<String> imageUrls;
}
=======
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
	private int likesCount;
	private int commentCount;
	private UserDto user;
	private List<BoardImageDto> images;
}
>>>>>>> refs/remotes/origin/bsh
