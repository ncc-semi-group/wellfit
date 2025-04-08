package com.example.demo.service;

import lombok.RequiredArgsConstructor;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.FriendshipDto;
import com.example.demo.dto.UserDto;
import com.example.demo.mapper.FriendshipMapper;
import com.example.demo.mapper.FriendshipRequestMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipMapper friendshipMapper;
    private final FriendshipRequestMapper friendshipRequestMapper;

    public List<FriendshipDto> getAllFriendships() {
        return friendshipMapper.getAllFriendships();
    }

    public List<UserDto> getFriendsByUserId(int userId) {
        return friendshipMapper.getFriendsByUserId(userId);
    }

    public void addFriendship(FriendshipDto friendshipDto) {
        friendshipMapper.insertFriendship(friendshipDto);
    }

    public void removeFriendship(int id) {
        friendshipMapper.deleteFriendship(id);
    }
    
    public List<UserDto> searchFriendsByNickname(@Param("userId") int userId, String nickname, String query) {
        return friendshipMapper.searchFriendsByNickname(userId, nickname, query);
    }

    // 친구 요청 수락
    @Transactional
    public void acceptFriendRequest(int requestId, int userId, int senderId) {
    	FriendshipDto friendship = new FriendshipDto();
        friendship.setUser1Id(userId);
        friendship.setUser2Id(senderId);
        friendshipMapper.insertFriendship(friendship);

        int deleted = friendshipRequestMapper.deleteFriendshipRequest(requestId);
        if (deleted == 0) {
            throw new IllegalStateException("해당 친구 요청이 존재하지 않거나 이미 처리되었습니다.");
        }
    }
}
