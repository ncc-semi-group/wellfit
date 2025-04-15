package com.example.demo.friendship.service;


import com.example.demo.dto.board.FollowDto;
import com.example.demo.dto.user.UserDto;
import com.example.demo.friendship.mapper.FollowMapper;
import com.example.demo.friendship.mapper.FollowRequestMapper;

import lombok.RequiredArgsConstructor;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowMapper followMapper;
    private final FollowRequestMapper followReqeustMapper;

    // 유저가 팔로우하고 있는 사람 목록 조회
    public List<UserDto> getFollowingByUserId(int userId) {
        return followMapper.getFollowingByUserId(userId);
    }

    // 유저를 팔로우하는 사람 목록 조회
    public List<UserDto> getFollowerByUserId(int userId) {
        return followMapper.getFollowerByUserId(userId);
    }

    // 팔로잉 중 이름 검색
    public List<UserDto> searchFollowingByNickname(int userId, String nickname) {
    	try {
    		return followMapper.searchFollowingByNickname(userId, nickname);
        } catch (Exception e) {
            System.out.println("닉네임 검색 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // 팔로워 중 이름 검색
    public List<UserDto> searchFollowerByNickname(int userId, String nickname) {
    	try {
    		return followMapper.searchFollowerByNickname(userId, nickname);
        } catch (Exception e) {
            System.out.println("닉네임 검색 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // 팔로우 추가
    public void addFollowing(FollowDto followDto) {
        followMapper.insertFollowing(followDto);
    }

    // 팔로우 삭제
    public void removeFollowing(int userId1, int userId2) {
        followMapper.deleteFollowing(userId1, userId2);
    }
    
    //팔로우 요청 수락
    @Transactional
    public void acceptFollowRequest(int requestId, int userId, int senderId) {
    	FollowDto follow = new FollowDto();
    	follow.setId(userId);
    	follow.setId(senderId);
    	followMapper.insertFollowing(follow);
    	
    	int deleted = followReqeustMapper.deleteFollowRequest(requestId);
    	if (deleted == 0) {
    		throw new IllegalStateException("해당 팔로우 요청이 존재하지 않거나 이미 처리되었습니다.");
        }
    }
    
    //팔로우 관계 확인
    public boolean isFollowing(int userId1, int userId2) {
    	return followMapper.isFollow(userId1, userId2) > 0;
    }

    //팔로우 토글
    @Transactional
    public boolean toggleFollow(int userId1, int userId2) {
        if (isFollowing(userId1, userId2)) {
            removeFollowing(userId1, userId2);
            return false;
        } else {
            FollowDto followDto = new FollowDto();
            followDto.setCurrentUserId(userId1);  // 팔로우 하는 사람
            followDto.setTargetUserId(userId2);   // 팔로우 받는 사람
            addFollowing(followDto);
            return true;
        }
    }
}
