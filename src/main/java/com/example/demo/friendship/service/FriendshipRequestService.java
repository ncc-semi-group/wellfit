package com.example.demo.friendship.service;

import lombok.RequiredArgsConstructor;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.example.demo.dto.friendship.FriendshipRequestDto;
import com.example.demo.dto.user.UserDto;
import com.example.demo.friendship.mapper.FriendshipRequestMapper;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipRequestService {
    private final FriendshipRequestMapper friendshipRequestMapper;

    public List<FriendshipRequestDto> getAllFriendshipRequests() {
        return friendshipRequestMapper.getAllFriendshipRequests();
    }

    public List<UserDto> searchFriendsByNickname(@Param("userId") int userId, String nickname, String query) {
        return friendshipRequestMapper.searchFriendsByNickname(userId, nickname, query);
    }
    
    public List<UserDto> getRecommendFriends(int userId) {
    	List<UserDto> recommendedFriends = friendshipRequestMapper.getRecommendFriends(userId);

        // 결과가 없을 경우 빈 리스트 반환
        return recommendedFriends.isEmpty() ? Collections.emptyList() : recommendedFriends;
    }
    
    public List<FriendshipRequestDto> getFriendRequestsByUserId(int userId) {
    	List<FriendshipRequestDto> FriendRequests = friendshipRequestMapper.getFriendRequestsByUserId(userId);

        // 결과가 없을 경우 빈 리스트 반환
        return FriendRequests.isEmpty() ? Collections.emptyList() : FriendRequests;
    }
    

    // 친구 요청 보내기
    public void sendFriendRequest(int userId, int senderId, String requestMessage) {
        FriendshipRequestDto request = new FriendshipRequestDto();
        request.setUserId(userId);
        request.setSenderId(senderId);
        request.setRequestMessage(requestMessage);

        friendshipRequestMapper.insertFriendshipRequest(request);
    }

    // 친구 요청 거절
    public void rejectFriendRequest(int requestId) {
        friendshipRequestMapper.deleteFriendshipRequest(requestId);
    }
    
}
