package com.example.demo.board.mapper;


import com.example.demo.dto.board.BoardDto;
import com.example.demo.dto.board.CommentDto;


import org.apache.ibatis.annotations.*;


import java.util.List;


@Mapper
public interface BoardMapper {

	
	
	public List<BoardDto> selectAllBoardsWithDetails();
	
	public List<BoardDto> getSelectUserId(@Param("userId") int userId);

	// 게시물 상세 정보 조회 (이미지, 좋아요 수 포함)
	public BoardDto selectBoardDetail(int boardId);
	
	// 최신 게시물 정보 조회 (메인페이지 사용)
	public List<BoardDto> selectBoardPreview();
	
	//댓글 정보 조회
	public List<CommentDto> selectCommentsByBoardId(int boardId);
	
	public int insertComment(CommentDto comment);
	
	int isLiked(@Param("postId") int boardId, @Param("userId") int userId);

    void insertLike(@Param("postId") int boardId, @Param("userId") int userId);

    void deleteLike(@Param("postId") int boardId, @Param("userId") int userId);

    int countLikes(@Param("postId") int boardId);

	
	
	
}
