package com.example.demo.board.mapper;


import com.example.demo.dto.board.BoardDto;
import com.example.demo.dto.board.CommentDto;
import com.example.demo.dto.hashtag.HashtagDto;
import com.example.demo.dto.user.UserDto;

import org.apache.ibatis.annotations.*;


import java.util.List;


@Mapper
public interface BoardMapper {

	
	
	public List<BoardDto> selectAllBoardsWithDetails(@Param("userId")int userId);
	
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

 // 특정 사용자와 타겟 사용자가 팔로우 관계인지 확인
    int isFollowed(@Param("userId") int userId, @Param("targetUserId") int targetUserId);

    // 팔로우 추가
    void insertFollow(@Param("userId") int userId, @Param("targetUserId") int targetUserId);

    // 팔로우 삭제
    void deleteFollow(@Param("userId") int userId, @Param("targetUserId") int targetUserId);

    // 팔로워 수 카운트
    int countFollowers(@Param("targetUserId") int targetUserId);
	
    public List<Long> selectFollowingUserIds(@Param("userId") Long userId);
    
    public List<UserDto> getRecommendedUsersByFollowers(int userId);
    
	//
    public List<BoardDto> selectTopLikedBoardsWithImages();
  
    
    
    
    public void insertBoard(BoardDto boardDto);

    public void insertBoardImage(@Param("boardId") int boardId, @Param("fileName") String fileName);


    public void insertBoardHashtag(@Param("boardId") int boardId, @Param("tagId") int tagId);
    
    
    
}
