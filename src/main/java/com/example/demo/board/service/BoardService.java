package com.example.demo.board.service;

import com.example.demo.board.mapper.BoardMapper;
import com.example.demo.dto.board.BoardDto;
import com.example.demo.dto.board.CommentDto;
import com.example.demo.dto.user.UserDto;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class BoardService {

	BoardMapper boardMapper;
	


	// 특정 게시판 조회
	public BoardDto getBoardById(int id) {
		return boardMapper.selectBoardDetail(id);
	}


	public List<BoardDto> getAllBoardWithDetails(int userId){
		return boardMapper.selectAllBoardsWithDetails(userId);
	}

	public List<BoardDto> getSelectUserId(int userId){
		return boardMapper.getSelectUserId(userId);
	}
	
	public List<CommentDto> getCommentByBoardId(int boardId){
		return boardMapper.selectCommentsByBoardId(boardId);
	}
	
	
	// 댓글 추가
	public void addComment(CommentDto commentDto) {
		boardMapper.insertComment(commentDto);
	}
	
	
	 @Transactional
	    public boolean toggleLike(int postId, int userId) {
	        boolean alreadyLiked = boardMapper.isLiked(postId, userId) > 0;

	        if (alreadyLiked) {
	        	boardMapper.deleteLike(postId, userId);
	            return false;
	        } else {
	        	boardMapper.insertLike(postId, userId);
	            return true;
	        }
	    }

	    public int getLikeCount(int postId) {
	        return boardMapper.countLikes(postId);
	    }
	
	    
	    // 팔로우/팔로우 취소 기능
	    @Transactional
	    public boolean toggleFollow(int userId, int targetUserId) {
	        // 이미 팔로우한 상태인지 확인
	        boolean alreadyFollowed = boardMapper.isFollowed(userId, targetUserId) > 0;

	        if (alreadyFollowed) {
	            // 팔로우 취소
	            boardMapper.deleteFollow(userId, targetUserId);
	            return false; // 팔로우 취소 상태
	        } else {
	            // 팔로우
	            boardMapper.insertFollow(userId, targetUserId);
	            return true; // 팔로우 상태
	        }
	    }

	    // 팔로워 수 가져오기
	    public int getFollowerCount(int targetUserId) {
	        return boardMapper.countFollowers(targetUserId);
	    }
	    
	    public List<Long> getFollowingUserIds(Long userId) {
	        return boardMapper.selectFollowingUserIds(userId);
	    }
	    
	    
	    public List<UserDto> getRecommendedUsersByFollowers(int userId) {
	        return boardMapper.getRecommendedUsersByFollowers(userId);
	    }
	    
	    public List<BoardDto> getTopLikedBoardsWithImages() {
	        return boardMapper.selectTopLikedBoardsWithImages();
	    }

	    
	    
	
}
