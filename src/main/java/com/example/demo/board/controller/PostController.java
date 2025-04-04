package com.example.demo.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.board.service.BoardService;
import com.example.demo.dto.BoardDto;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
public class PostController {

	@Autowired
	BoardService boardService;

	@GetMapping("/get-posts")
	public List<BoardDto> getPosts() {
		return boardService.getAllBoards(); // 데이터베이스에서 게시물 정보를 가져옵니다.
	}
	
	@GetMapping("/with-details")
	public List<BoardDto> getAllBoardsWithDetails() 
	{
		return boardService.getAllBoardWithDetails();
	
	}
	
}
