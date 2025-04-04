package com.example.demo.comment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.CommentDto;

@Mapper
public interface CommentMapper {
	public List<CommentDto> getSelectUserId(int userId);
}
