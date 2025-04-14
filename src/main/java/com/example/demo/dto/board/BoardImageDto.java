package com.example.demo.dto.board;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("BoardImageDto")
public class BoardImageDto {

		private int id;
		private int boardId;
		private String fileName;
		
}
