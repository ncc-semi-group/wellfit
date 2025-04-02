package data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import data.dto.UserBadgeDto;

@Mapper
public interface UserBadgeMapper {
	public List<UserBadgeDto> getSelectUserId(int userId);
}
