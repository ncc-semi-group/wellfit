package com.example.demo.badge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.badge.mapper.UserBadgeMapper;
import com.example.demo.dto.UserBadgeDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserBadgeService {
	UserBadgeMapper userbadgeMapper;
	
	public List<UserBadgeDto> getSelectUserId(int userId){
		return userbadgeMapper.getSelectUserId(userId);
	}
}
