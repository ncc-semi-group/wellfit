package com.example.demo.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.board.mapper.BoardMapper;
import com.example.demo.dto.BoardDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BoardService {
	BoardMapper boardMapper;
	
	public List<BoardDto> getSelectUserId(int userId){
		return boardMapper.getSelectUserId(userId);
	}
}
