package com.example.demo.board.service;

import com.example.demo.board.mapper.BoardMapper;
import com.example.demo.dto.BoardDto;
import com.example.demo.dto.CommentDto;


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


	public List<BoardDto> getAllBoardWithDetails(){
		return boardMapper.selectAllBoardsWithDetails();
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
	
	
}
