package com.example.demo.dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("LikeRequestDto")
public class LikeRequestDto {
	 private int userId;
}
