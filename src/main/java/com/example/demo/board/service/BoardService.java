package com.example.demo.board.service;

import com.example.demo.board.mapper.BoardMapper;
import com.example.demo.dto.board.BoardDto;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BoardService {

	BoardMapper boardMapper;

	// 모든 게시판 목록 조회
	public List<BoardDto> getAllBoards() {
		return boardMapper.selectAllBoards();
	}

	// 특정 게시판 조회
	public BoardDto getBoardById(int id) {
		return boardMapper.selectBoardDetail(id);
	}

	// 게시판 추가
	public void addBoard(BoardDto boardDto) {
		boardMapper.insertBoard(boardDto);
	}

	// 게시판 수정
	public void updateBoard(BoardDto boardDto) {
		boardMapper.updateBoard(boardDto);
	}

	// 게시판 삭제
	public void deleteBoard(int id) {
		boardMapper.deleteBoard(id);
	}

	public List<BoardDto> getAllBoardWithDetails(){
		return boardMapper.selectAllBoardsWithDetails();
	}

	public List<BoardDto> getSelectUserId(int userId){
		return boardMapper.getSelectUserId(userId);
	}
}
