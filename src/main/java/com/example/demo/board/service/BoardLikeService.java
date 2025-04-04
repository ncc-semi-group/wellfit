package com.example.demo.board.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.example.demo.board.mapper.BoardLikeMapper;
import com.example.demo.dto.BoardDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardLikeService {
    private final BoardLikeMapper boardLikeMapper;

    public List<Map<String, Object>> getLikesByTag(int userId) {
        return boardLikeMapper.getLikesByTag(userId);
    }

    public List<BoardDto> getLikedBoardsByTag(int userId, String tag) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("tag", tag);
        
        List<BoardDto> boards = boardLikeMapper.getLikedBoardsByTag(params);
        
        // 각 게시물의 이미지 정보 설정
        for (BoardDto board : boards) {
            List<String> images = boardLikeMapper.getBoardImages(board.getId());
            if (images != null && !images.isEmpty()) {
                board.setImageUrls(images);
                board.setImageCount(images.size());
            }
        }
        
        return boards;
    }
} 