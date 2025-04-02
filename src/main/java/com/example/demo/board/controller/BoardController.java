package com.example.demo.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BoardController {
    @GetMapping("/feed/all")
    public String board(Model model) {
    	model.addAttribute("showHeader", false);
    	model.addAttribute("currentPage", "community");
        return "views/board/boardmain";
    }
    
    @GetMapping("/feed/following")
    public String boardfollowing(Model model) {
    	model.addAttribute("showHeader", false);
    	model.addAttribute("currentPage","community");
        return "views/board/boardfollowing";
    }
    
    @GetMapping("/feed/popular")
    public String boardmost(Model model) {
    	model.addAttribute("currentPage","community");
        return "views/board/boardmost";
    }
    
}
