package com.example.demo.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.user.UserDto;
import com.example.demo.user.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
	private final UserService userService;

	@GetMapping("/loginpage")
	public String loginPage(Model model) {
		model.addAttribute("showHeader", false);
		model.addAttribute("showFooter", false);
		return "views/loginpage/loginpage";
	}

	@PostMapping("/logincheck")
	public String loginCheck(@RequestParam("email") String email,
	                         @RequestParam("password") String password,
	                         HttpSession session,
	                         Model model) {

	    UserDto user = userService.login(email, password);

	    if (user == null) {
	        return "redirect:/login";
	    }

	    session.setAttribute("userId", user.getId());
	    return "redirect:/friendpage";
	}


	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/loginpage";
	}

	@GetMapping("/signuppage")
	public String signup(Model model) {
		model.addAttribute("showHeader", false);
		model.addAttribute("showFooter", false);
		return "views/loginpage/signuppage";
	}
	
	@PostMapping("/signup")
	public String signup(@RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("nickname") String nickname,
            Model model) {
        boolean result = userService.signup(email, password, nickname);

        if (result) {
            return "redirect:/welcome";
        } else {
            model.addAttribute("signupError", true);
    		model.addAttribute("showHeader", false);
    		model.addAttribute("showFooter", false);
            return "views/loginpage/signuppage";
        }
    }

	@GetMapping("/welcome")
	public String login2(Model model) {
		model.addAttribute("showHeader", false);
		model.addAttribute("showFooter", false);
		return "views/loginpage/welcome";
	}

	@GetMapping("/inputdata1")
	public String login3(Model model) {
		model.addAttribute("showHeader", false);
		model.addAttribute("showFooter", false);
		return "views/loginpage/inputdata1";
	}

	@GetMapping("/inputdata2")
	public String login4(Model model) {
		model.addAttribute("showHeader", false);
		model.addAttribute("showFooter", false);
		return "views/loginpage/inputdata2";
	}
}
