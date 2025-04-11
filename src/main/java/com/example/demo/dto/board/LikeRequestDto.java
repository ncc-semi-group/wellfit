package com.example.demo.dto.board;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("LikeRequestDto")
public class LikeRequestDto {
	 private int userId;
}
