package data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import data.dto.CommentDto;

@Mapper
public interface CommentMapper {
	public List<CommentDto> getSelectUserId(int userId);
}
