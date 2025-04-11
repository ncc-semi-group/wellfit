package com.example.demo.board.mapper;

import com.example.demo.dto.board.CommentDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

	// 모든 댓글 조회
	public List<CommentDto> selectAllComments();

	// 특정 게시판의 댓글 조회
	public List<CommentDto> selectCommentsByBoardId(@Param("boardId") int boardId);

	// 댓글 삽입
	public void insertComment(CommentDto commentDto);

	// 댓글 수정
	public void updateComment(CommentDto commentDto);

	// 댓글 삭제
	public void deleteComment(@Param("id") int id);
	

	public List<CommentDto> getSelectUserId(int userId);
	
	
}
