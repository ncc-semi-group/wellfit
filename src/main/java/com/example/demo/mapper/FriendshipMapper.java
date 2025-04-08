package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.FriendshipDto;
import com.example.demo.dto.UserDto;

@Mapper
public interface FriendshipMapper {
    List<FriendshipDto> getAllFriendships();
    List<UserDto> getFriendsByUserId(@Param("userId") int userId);
    List<UserDto> searchFriendsByNickname(@Param("userId") int userId, String nickname, String query);
    void insertFriendship(FriendshipDto friendship);
    void deleteFriendship(@Param("id") int id);
    List<UserDto> searchFriendsByUserId(@Param("userId") int userId, @Param("query") String query);
}
