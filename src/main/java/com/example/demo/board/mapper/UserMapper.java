package com.example.demo.board.mapper;

import com.example.demo.dto.UserDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

	// 사용자 추가
	public void insertUser(UserDto userDto);

	// 사용자 정보 조회
	public UserDto selectUserById(@Param("id") int id);

	// 이메일로 사용자 조회
	public UserDto selectUserByEmail(@Param("email") String email);

	// 사용자 정보 업데이트
	public void updateUser(UserDto userDto);

	// 사용자 목록 조회
	List<UserDto> selectAllUsers();

	// 사용자 삭제
	public void deleteUser(@Param("id") int id);
}
