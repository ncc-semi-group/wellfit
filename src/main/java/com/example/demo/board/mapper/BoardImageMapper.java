package com.example.demo.board.mapper;

import org.apache.ibatis.annotations.*;

import com.example.demo.dto.board.BoardImageDto;

import java.util.List;

@Mapper
public interface BoardImageMapper {

	// 모든 게시판 이미지 목록 조회
	public List<BoardImageDto> selectAllBoardImages();

	// 특정 게시판의 이미지 목록 조회 (boardId로 검색)
	public List<BoardImageDto> selectImagesByBoardId(@Param("boardId") int boardId);

	// 게시판 이미지 추가
	public void insertBoardImage(BoardImageDto boardImageDto);

	// 게시판 이미지 수정
	public void updateBoardImage(BoardImageDto boardImageDto);

	// 게시판 이미지 삭제
	public void deleteBoardImage(@Param("id") int id);

	// 특정 게시판 이미지 삭제 (boardId로 삭제)
	public void deleteImagesByBoardId(@Param("boardId") int boardId);
}
