package com.example.demo.board.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.BoardDto;

@Mapper
public interface BoardLikeMapper {
    List<Map<String, Object>> getLikesByTag(int userId);
    List<BoardDto> getLikedBoardsByTag(Map<String, Object> params);
    List<String> getBoardImages(int boardId);
} 