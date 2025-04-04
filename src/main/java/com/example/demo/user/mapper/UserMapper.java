package com.example.demo.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.UserDto;
import java.util.List;

@Mapper
public interface UserMapper {
	public UserDto getSelectUser(int id);
	public UserDto getSelectNickname(String nickname);
	public List<UserDto> getLikeList(int userId);
	public void mypageUpdateUser(UserDto dto);
	public void mypageupdateProfileImage(@Param("id") int id, @Param("imageUrl") String imageUrl);
	public void updateUser(UserDto dto);
	public UserDto getUserById(String id);
}
