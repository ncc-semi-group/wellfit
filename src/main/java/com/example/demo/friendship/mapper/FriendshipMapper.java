package com.example.demo.friendship.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.friendship.FriendshipDto;
import com.example.demo.dto.user.UserDto;

@Mapper
public interface FriendshipMapper {
    List<FriendshipDto> getAllFriendships();
    List<UserDto> getFriendsByUserId(@Param("userId") int userId);
    List<UserDto> searchFriendsByNickname(@Param("userId") int userId, String nickname, String query);
    void insertFriendship(FriendshipDto friendship);
    void deleteFriendship(@Param("id") int id);
    List<UserDto> searchFriendsByUserId(@Param("userId") int userId, @Param("query") String query);
    
    // 친구 관계 확인
    int isFriend(@Param("userId1") int userId1, @Param("userId2") int userId2);
    
    // 친구 삭제
    void deleteFriendshipByUserIds(@Param("userId1") int userId1, @Param("userId2") int userId2);
}
