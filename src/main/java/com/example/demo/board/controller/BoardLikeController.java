package com.example.demo.board.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.board.service.BoardLikeService;
import com.example.demo.dto.BoardDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardLikeController {
    private final BoardLikeService boardLikeService;

    @GetMapping("/api/likes/boards")
    @ResponseBody
    public List<BoardDto> getLikedBoardsByTag(
        @RequestParam int userId,
        @RequestParam String tag
    ) {
        return boardLikeService.getLikedBoardsByTag(userId, tag);
    }
}