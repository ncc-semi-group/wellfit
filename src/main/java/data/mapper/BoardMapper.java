package data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import data.dto.BoardDto;

@Mapper
public interface BoardMapper {
	public List<BoardDto> getSelectUserId(int userId);
}
