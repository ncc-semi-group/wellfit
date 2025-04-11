package com.example.demo.badge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.user.UserBadgeDto;

@Mapper
public interface UserBadgeMapper {
	public List<UserBadgeDto> getSelectUserId(int userId);
	public void updateUserBadge(UserBadgeDto userBadge);
	public void insertUserBadge(UserBadgeDto userBadge);
}
