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
