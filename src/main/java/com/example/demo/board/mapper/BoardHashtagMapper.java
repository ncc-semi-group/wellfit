package com.example.demo.board.mapper;

import com.example.demo.dto.board.BoardHashtagDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardHashtagMapper {

	// 특정 게시판에 해당하는 해시태그 목록 조회 (boardId로 검색)
	public List<BoardHashtagDto> selectHashtagsByBoardId(@Param("boardId") int boardId);

	// 특정 해시태그에 해당하는 게시판 목록 조회 (tagId로 검색)
	public List<BoardHashtagDto> selectBoardsByTagId(@Param("tagId") int tagId);

	// 게시판에 해시태그 추가
	public void insertBoardHashtag(BoardHashtagDto boardHashtagDto);

	// 게시판의 해시태그 삭제 (boardId와 tagId로 삭제)
	public void deleteBoardHashtag(@Param("boardId") int boardId, @Param("tagId") int tagId);

	// 특정 게시판의 모든 해시태그 삭제 (boardId로 삭제)
	public void deleteHashtagsByBoardId(@Param("boardId") int boardId);
}
