package com.example.demo.board.service;

import com.example.demo.board.mapper.CommentMapper;
import com.example.demo.dto.CommentDto;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentMapper commentMapper;

    @Autowired
    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    // 모든 댓글 조회
    public List<CommentDto> getAllComments() {
        return commentMapper.selectAllComments();
    }

    // 특정 게시판의 댓글 조회
    public List<CommentDto> getCommentsByBoardId(int boardId) {
        return commentMapper.selectCommentsByBoardId(boardId);
    }

    // 댓글 삽입
    public void addComment(CommentDto commentDto) {
        commentMapper.insertComment(commentDto);
    }

    // 댓글 수정
    public void updateComment(CommentDto commentDto) {
        commentMapper.updateComment(commentDto);
    }

    // 댓글 삭제
    public void deleteComment(int id) {
        commentMapper.deleteComment(id);
    }
    
	public List<CommentDto> getSelectUserId(int userId){
		return commentMapper.getSelectUserId(userId);
	}
}
