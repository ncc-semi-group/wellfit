package com.example.demo.board.controller;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.board.service.BoardService;
import com.example.demo.dto.board.BoardDto;
import com.example.demo.naver.storage.NcpObjectStorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/posting")
@RequiredArgsConstructor
public class PostingFeedController {

    private final NcpObjectStorageService storageService;
    private final BoardService boardService;

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadBoard(
        @RequestParam("userId") int userId,
        @RequestParam("content") String content,
        @RequestParam(value = "tags[]", required = false) List<Integer> tags, // tags[] 대신 tags로 수정
        @RequestParam(value = "images[]", required = false) List<MultipartFile> images
    ) {
        System.out.println("Received Tags: " + tags); // Array or List 형태로 출력
        System.out.println("Received Images: " + images);

        try {
            BoardDto boardDto = new BoardDto();
            boardDto.setUserId(userId);
            boardDto.setContent(content);

            boardService.uploadBoard(
                boardDto,
                tags != null ? tags : new ArrayList<>(),
                images != null ? images : new ArrayList<>()
            );
            return ResponseEntity.ok("업로드 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("업로드 실패");
        }
    }
}
