package data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import data.dto.UserDto;

@Mapper
public interface UserPageMapper {
	public UserDto getUserProfile(int userId);
	
	public List<UserDto> getOtherUsers(int currentUserId);
}
