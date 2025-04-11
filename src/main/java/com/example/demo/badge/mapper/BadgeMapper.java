package com.example.demo.badge.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.user.BadgeDto;

import java.util.List;

@Mapper
public interface BadgeMapper {
    public List<BadgeDto> getAllBadges();
} 