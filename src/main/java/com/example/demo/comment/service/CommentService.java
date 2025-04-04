package com.example.demo.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.comment.mapper.CommentMapper;
import com.example.demo.dto.CommentDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {
	CommentMapper commentMapper;
	
	public List<CommentDto> getSelectUserId(int userId){
		return commentMapper.getSelectUserId(userId);
	}
}
