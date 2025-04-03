package com.example.demo.board.mapper;

import com.example.demo.dto.BoardLikeDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardLikeMapper {

	// 특정 게시판에 대한 좋아요 추가
	public void insertBoardLike(BoardLikeDto boardLikeDto);

	// 특정 게시판에 대한 좋아요 삭제
	public void deleteBoardLike(@Param("userId") int userId, @Param("boardId") int boardId);

	// 특정 게시판에 대한 모든 좋아요 수 조회
	public int selectBoardLikeCount(@Param("boardId") int boardId);

	// 특정 사용자가 특정 게시판에 좋아요를 했는지 확인
	public boolean checkIfUserLikedBoard(@Param("userId") int userId, @Param("boardId") int boardId);

	// 특정 게시판에 좋아요한 사용자 목록 조회
	public List<BoardLikeDto> selectUsersWhoLikedBoard(@Param("boardId") int boardId);
}
