package data.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import data.dto.BoardDto;

@Mapper
public interface BoardLikeMapper {
    List<Map<String, Object>> getLikesByTag(int userId);
    List<BoardDto> getLikedBoardsByTag(Map<String, Object> params);
    List<String> getBoardImages(int boardId);
} 