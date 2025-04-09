package com.example.demo.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.demo.config.PasswordHashingUtil;
import com.example.demo.dto.UserDto;
import com.example.demo.user.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserMapper userMapper;

	public List<UserDto> getAllUsers() {
		return userMapper.getAllUsers();
	}

	public UserDto getUserById(int id) {
		return userMapper.getUserById(id);
	}

	public UserDto getUserByEmail(String email) {
		return userMapper.getUserByEmail(email);
	}

	public UserDto login(String email, String password) {
		UserDto user = userMapper.getUserByEmail(email);
		if (user == null) return null;

		String salt = userMapper.getSaltByUserId(user.getId());
		if (salt == null) return null;

		String hashedInput = PasswordHashingUtil.hashPassword(password, salt);
		if (hashedInput.equals(user.getPassword())) {
			return user;
		}
		return null;
	}

	public boolean signup(String email, String password, String nickname) {
		if (email == null || email.trim().isEmpty()
				|| password == null || password.trim().isEmpty()
				|| nickname == null || nickname.trim().isEmpty()) {
			return false;
		}

		// 이메일 중복 체크
		if (userMapper.getUserByEmail(email) != null) {
			return false;
		}

		String salt = PasswordHashingUtil.generateSalt();
		String hashedPassword = PasswordHashingUtil.hashPassword(password, salt);

		UserDto user = new UserDto();
		user.setEmail(email);
		user.setPassword(hashedPassword);
		user.setNickname(nickname);

		int result = userMapper.insertUser(user); // user 저장
		userMapper.insertUserSalt(user.getId(), salt); // salt 저장

		return result > 0;
	}

	public String getSaltByUserId(int userId) {
		return userMapper.getSaltByUserId(userId);
	}

	public void deleteUser(int id) {
		userMapper.deleteUser(id);
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
}
