package com.example.demo.board.service;

import com.example.demo.board.mapper.BoardLikeMapper;
import com.example.demo.dto.board.BoardDto;
import com.example.demo.dto.board.BoardLikeDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardLikeService {
    @Autowired
    public BoardLikeService(BoardLikeMapper boardLikeMapper) {
        this.boardLikeMapper = boardLikeMapper;
    }

    // 특정 게시판에 좋아요 추가
    public void addBoardLike(BoardLikeDto boardLikeDto) {
        boardLikeMapper.insertBoardLike(boardLikeDto);
    }

    // 특정 게시판에 좋아요 삭제
    public void removeBoardLike(int userId, int boardId) {
        boardLikeMapper.deleteBoardLike(userId, boardId);
    }

    // 특정 게시판에 대한 좋아요 수 조회
    public int getBoardLikeCount(int boardId) {
        return boardLikeMapper.selectBoardLikeCount(boardId);
    }

    // 특정 사용자가 특정 게시판에 좋아요를 했는지 확인
    public boolean checkIfUserLikedBoard(int userId, int boardId) {
        return boardLikeMapper.checkIfUserLikedBoard(userId, boardId);
    }

    // 특정 게시판에 좋아요한 사용자 목록 조회
    public List<BoardLikeDto> getUsersWhoLikedBoard(int boardId) {
        return boardLikeMapper.selectUsersWhoLikedBoard(boardId);
    }
    
    private final BoardLikeMapper boardLikeMapper;

    public List<BoardLikeDto> getLikesByTag(int userId) {
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
