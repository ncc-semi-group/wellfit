package com.example.demo.board.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.board.service.CommentService;
import com.example.demo.dto.CommentDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/api/comment/add")
    public String addComment(@RequestBody CommentDto commentDto) {
        try {
            commentService.addComment(commentDto);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
} 