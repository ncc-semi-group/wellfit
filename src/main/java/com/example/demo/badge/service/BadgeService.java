package com.example.demo.badge.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.example.demo.badge.mapper.BadgeMapper;
import com.example.demo.dto.BadgeDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeMapper badgeMapper;
    
    public List<BadgeDto> getAllBadges() {
        return badgeMapper.getAllBadges();
    }
} 