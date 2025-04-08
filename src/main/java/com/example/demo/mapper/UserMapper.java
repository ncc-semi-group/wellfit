package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.example.demo.dto.UserDto;

@Mapper
public interface UserMapper {
    List<UserDto> getAllUsers();
    UserDto getUserById(@Param("id") int id);
    void insertUser(UserDto user);
    void deleteUser(@Param("id") int id);
    List<UserDto> searchUsersByNickname(@Param("nickname") String nickname);
    UserDto getUserById(String email);
}
