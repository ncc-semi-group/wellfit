package com.example.demo.dto.board;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("BoardHashtagDto")
public class BoardHashtagDto {

	private int boardId;
	private int tagId;
}