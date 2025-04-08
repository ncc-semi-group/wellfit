package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDto;
import com.example.demo.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    public UserDto login(String email, String password) {
        UserDto user = userMapper.getUserById(email);
        System.out.println(user.getPassword() + password);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    public List<UserDto> getAllUsers() {
        return userMapper.getAllUsers();
    }

    public UserDto getUserById(int id) {
        return userMapper.getUserById(id);
    }
    
    public List<UserDto> searchUsersByNickname(String nickname) {
        try {
            return userMapper.searchUsersByNickname(nickname);
        } catch (Exception e) {
            System.out.println("닉네임 검색 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void insertUser(UserDto userDto) {
        userMapper.insertUser(userDto);
    }

    public void deleteUser(int id) {
        userMapper.deleteUser(id);
    }
}
