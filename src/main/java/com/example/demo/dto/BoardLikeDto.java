package com.example.demo.dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("BoardLikeDto")
public class BoardLikeDto {
	private int id;
	private int userId;
	private int boardId;
	
}
