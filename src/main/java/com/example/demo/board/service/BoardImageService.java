package com.example.demo.board.service;

import com.example.demo.board.mapper.BoardImageMapper;
import com.example.demo.dto.board.BoardImageDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardImageService {

    private final BoardImageMapper boardImageMapper;

    @Autowired
    public BoardImageService(BoardImageMapper boardImageMapper) {
        this.boardImageMapper = boardImageMapper;
    }

    // 모든 게시판 이미지 목록 조회
    public List<BoardImageDto> getAllBoardImages() {
        return boardImageMapper.selectAllBoardImages();
    }

    // 특정 게시판의 이미지 목록 조회
    public List<BoardImageDto> getImagesByBoardId(int boardId) {
        return boardImageMapper.selectImagesByBoardId(boardId);
    }

    // 게시판 이미지 추가
    public void addBoardImage(BoardImageDto boardImageDto) {
        boardImageMapper.insertBoardImage(boardImageDto);
    }

    // 게시판 이미지 수정
    public void updateBoardImage(BoardImageDto boardImageDto) {
        boardImageMapper.updateBoardImage(boardImageDto);
    }

    // 게시판 이미지 삭제
    public void removeBoardImage(int id) {
        boardImageMapper.deleteBoardImage(id);
    }

    // 특정 게시판의 모든 이미지 삭제
    public void removeImagesByBoardId(int boardId) {
        boardImageMapper.deleteImagesByBoardId(boardId);
    }
}
