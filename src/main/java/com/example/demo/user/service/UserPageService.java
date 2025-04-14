package com.example.demo.user.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.user.UserDto;
import com.example.demo.user.mapper.UserPageMapper;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserPageService {
    private final UserPageMapper userPageMapper;

    public UserDto getUserProfile(int userId) {
        return userPageMapper.getUserProfile(userId);
    }
    
    public List<UserDto> getOtherUsers(int currentUserId) {
        return userPageMapper.getOtherUsers(currentUserId);
    }
    
    public List<Map<String, Object>> getUserExercises(int userId, String start, String end) {
        // 운동 기록 조회 로직 구현
        return userPageMapper.getUserExercises(userId, start, end);
    }
}