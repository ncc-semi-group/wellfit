package data.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import data.dto.UserDto;
import data.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "views/login/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam(value = "id") String id, @RequestParam(value = "password") String password, HttpSession session) {
        UserDto user = userService.login(id, password);
        if (user != null) {
            session.setAttribute("userId", user.getId());
            return "redirect:/mypage";
        } else {
            return "redirect:/login?error";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
} 