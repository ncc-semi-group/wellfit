package com.example.demo.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BoardController {
    @GetMapping("/board")
    public String board(Model model) {
    	model.addAttribute("showHeader", false);
    	model.addAttribute("currentPage", "community");
        return "views/board/boardmain";
    }
    
    @GetMapping("/boardfollowing")
    public String boardfollowing(Model model) {
    	model.addAttribute("currentPage","community");
        return "views/board/boardfollowing";
    }
    
    @GetMapping("/boardmost")
    public String boardmost(Model model) {
    	model.addAttribute("currentPage","community");
        return "views/board/boardmost";
    }
    
}
