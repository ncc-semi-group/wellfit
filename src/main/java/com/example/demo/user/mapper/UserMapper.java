package com.example.demo.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.example.demo.dto.UserDto;

@Mapper
public interface UserMapper {
    List<UserDto> getAllUsers();
    UserDto getUserById(@Param("id") int id);
    UserDto getUserByEmail(@Param("email") String email);
    String getSaltByUserId(@Param("userId") int userId);
    int insertUser(UserDto user);
    void insertUserSalt(@Param("userId") int userId, @Param("salt") String salt);
    void deleteUser(@Param("id") int id);
    List<UserDto> searchUsersByNickname(@Param("nickname") String nickname);
}
