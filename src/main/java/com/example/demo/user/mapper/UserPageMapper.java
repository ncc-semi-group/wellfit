package com.example.demo.user.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.user.UserDto;

@Mapper
public interface UserPageMapper {
	public UserDto getUserProfile(int userId);
	
	public List<UserDto> getOtherUsers(int currentUserId);

	public List<Map<String, Object>> getUserExercises(@Param("userId") int userId, 
            @Param("start") String start, 
            @Param("end") String end);
}
