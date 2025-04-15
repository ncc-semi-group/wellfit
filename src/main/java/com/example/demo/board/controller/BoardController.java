package com.example.demo.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.board.service.BoardImageService;
import com.example.demo.board.service.BoardService;
import com.example.demo.dto.board.BoardDto;

import com.example.demo.dto.board.BoardImageDto;
import com.example.demo.dto.user.UserDto;

import com.example.demo.user.service.UserPageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardImageService boardImageService;
    @Autowired
    private UserPageService userPageService;

    @GetMapping("/feed/all")
    public String board(@RequestParam(name = "userId", required = false, defaultValue = "2") int userId,  Model model) {
    List<BoardDto> posts = boardService.getAllBoardWithDetails(userId);
        // 프로필 이미지 경로 처리
        
    for (BoardDto post : posts) {
            if (post.getUser() != null && post.getUser().getProfileImage() != null) {
                String profileImage = post.getUser().getProfileImage();
                if (!profileImage.startsWith("http") && !profileImage.startsWith("/images/")) {
                    post.getUser().setProfileImage("/images/" + profileImage);
                }
            }
         // 게시물 이미지 경로 가공
            if (post.getImages() != null) {
                for (BoardImageDto image : post.getImages()) {
                    String fileName = image.getFileName();
                    if (!fileName.startsWith("http") && !fileName.startsWith("/images/board/")) {
                        image.setFileName("/images/board/" + fileName);
                    }
                }
            }
        }
    
        
        model.addAttribute("posts", posts);
        model.addAttribute("showHeader", false);
        model.addAttribute("currentPage", "community");
        return "views/board/boardmain";
    }

  
    @GetMapping("/feed/following")
    public String boardfollowing(Model model) {
        model.addAttribute("showHeader", false);
        model.addAttribute("currentPage", "community");
        return "views/board/boardfollowing";
    }
    
    
    @GetMapping("/feed/posting")
    public String boardPost() {
        return "views/board/BoardPost";
    }

    @GetMapping("/feed/popular")
    public String boardmost(Model model) {
        model.addAttribute("showHeader", false);
        model.addAttribute("currentPage", "community");
        return "views/board/boardmost";
    }

    @GetMapping("/board/detail/{boardId}")
    public String boardDetail(@PathVariable("boardId") int boardId, Model model) {
        try {
            BoardDto post = boardService.getBoardById(boardId);
            
            if (post != null) {
                model.addAttribute("post", post);
                model.addAttribute("showHeader", false);
                model.addAttribute("currentPage", "record");
                return "views/board/boarddetail";
            } else {
                model.addAttribute("error", "게시물을 찾을 수 없습니다.");
                return "redirect:/feed/all";
            }
        } catch (Exception e) {
            e.printStackTrace(); // 로그 확인을 위해 추가
            model.addAttribute("error", "게시물을 찾을 수 없습니다.");
            return "redirect:/feed/all";
        }
    }
}
