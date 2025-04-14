package com.example.demo.friendship.service;


import com.example.demo.dto.FollowDto;
import com.example.demo.dto.UserDto;
import com.example.demo.friendship.mapper.FollowMapper;

import lombok.RequiredArgsConstructor;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowMapper followMapper;

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
}
