package com.example.demo.dto;


import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("CommentDto")
public class CommentDto {

	private int id;               // 댓글 ID (자동 증가)
    private int boardId;          // 게시판 ID (댓글이 속한 게시판)
    private int userId;           // 회원 ID (댓글 작성자)
    private String comment;           // 댓글 내용
    private int parentId;         // 부모 댓글 ID (대댓글에 사용)
    private Timestamp createdAt;  // 생성 시간 
	
	private List<HashtagDto> hashtags;
	private String nickname;   
}
