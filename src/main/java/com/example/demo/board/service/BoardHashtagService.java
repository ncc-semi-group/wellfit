package com.example.demo.board.service;

import com.example.demo.board.mapper.BoardHashtagMapper;
import com.example.demo.dto.BoardHashtagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardHashtagService {

    private final BoardHashtagMapper boardHashtagMapper;

    @Autowired
    public BoardHashtagService(BoardHashtagMapper boardHashtagMapper) {
        this.boardHashtagMapper = boardHashtagMapper;
    }

    // 특정 게시판에 해당하는 해시태그 목록 조회
    public List<BoardHashtagDto> getHashtagsByBoardId(int boardId) {
        return boardHashtagMapper.selectHashtagsByBoardId(boardId);
    }

    // 특정 해시태그에 해당하는 게시판 목록 조회
    public List<BoardHashtagDto> getBoardsByTagId(int tagId) {
        return boardHashtagMapper.selectBoardsByTagId(tagId);
    }

    // 게시판에 해시태그 추가
    public void addBoardHashtag(BoardHashtagDto boardHashtagDto) {
        boardHashtagMapper.insertBoardHashtag(boardHashtagDto);
    }

    // 게시판의 해시태그 삭제 (boardId와 tagId로 삭제)
    public void removeBoardHashtag(int boardId, int tagId) {
        boardHashtagMapper.deleteBoardHashtag(boardId, tagId);
    }

    // 특정 게시판의 모든 해시태그 삭제
    public void removeHashtagsByBoardId(int boardId) {
        boardHashtagMapper.deleteHashtagsByBoardId(boardId);
    }
}
