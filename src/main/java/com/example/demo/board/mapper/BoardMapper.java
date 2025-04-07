package com.example.demo.board.mapper;

import com.example.demo.dto.BoardDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

	// 모든 게시판 목록 조회
	public List<BoardDto> selectAllBoards();

	// 특정 게시판 조회 (ID로 검색)
	//public BoardDto selectBoardById(@Param("id") int id);

	// 게시판 추가
	public void insertBoard(BoardDto boardDto);

	// 게시판 수정
	public void updateBoard(BoardDto boardDto);

	// 게시판 삭제
	public void deleteBoard(@Param("id") int id);
	
	public List<BoardDto> selectAllBoardsWithDetails();
	
	public List<BoardDto> getSelectUserId(@Param("userId") int userId);

	// 게시물 상세 정보 조회 (이미지, 좋아요 수 포함)
	public BoardDto selectBoardDetail(int boardId);
}
