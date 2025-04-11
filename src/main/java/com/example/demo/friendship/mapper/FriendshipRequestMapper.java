package com.example.demo.friendship.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.FriendshipRequestDto;
import com.example.demo.dto.UserDto;

@Mapper
public interface FriendshipRequestMapper {
    List<FriendshipRequestDto> getAllFriendshipRequests();
    List<UserDto> getFriendsRequestByUserId(@Param("userId") int userId);
    List<UserDto> searchFriendsByNickname(@Param("userId") int userId, String nickname, String query);
    List<UserDto> getRecommendFriends(@Param("userId") int userId);
    List<FriendshipRequestDto> getFriendRequestsByUserId(@Param("userId") int userId);
    void insertFriendshipRequest(FriendshipRequestDto friendshipRequest);
    int deleteFriendshipRequest(@Param("id") int id);
}
