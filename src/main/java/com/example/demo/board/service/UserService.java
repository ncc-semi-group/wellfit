package com.example.demo.board.service;

import com.example.demo.board.mapper.UserMapper;
import com.example.demo.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 사용자 추가
    public void addUser(UserDto userDto) {
        userMapper.insertUser(userDto);
    }

    // 사용자 정보 조회 (ID로 검색)
    public UserDto getUserById(int id) {
        return userMapper.selectUserById(id);
    }

    // 이메일로 사용자 조회
    public UserDto getUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    // 사용자 정보 업데이트
    public void updateUser(UserDto userDto) {
        userMapper.updateUser(userDto);
    }

    // 사용자 목록 조회
    public List<UserDto> getAllUsers() {
        return userMapper.selectAllUsers();
    }

    // 사용자 삭제
    public void deleteUser(int id) {
        userMapper.deleteUser(id);
    }
}
