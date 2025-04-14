package com.example.demo.user.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.UserDto;
import com.example.demo.user.mapper.UserMapper;
import com.example.demo.user.service.UserService;
import com.example.demo.util.CalorieCalculator;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
	private final UserService userService;
	private final UserMapper userMapper;

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
            HttpSession session,
            Model model) {
        boolean result = userService.signup(email, password, nickname);

        if (result) {
        	UserDto user = userService.getUserByEmail(email);
        	session.setAttribute("userId", user.getId());
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
	
	@PostMapping("/updateinputdata1")
	public String updateInputData1(@ModelAttribute UserDto userDto, HttpSession session) {
		Integer userId = (Integer) session.getAttribute("userId");
		userDto.setId(userId);
		if (userId == null) {
			return "redirect:/login";
		}
		System.out.println(userId);
		userMapper.updateUserInitInfo1(userDto);

		return "redirect:/inputdata2";
	}

	@GetMapping("/inputdata2")
	public String login4(HttpSession session, Model model) {
	    model.addAttribute("showHeader", false);
	    model.addAttribute("showFooter", false);

	    Integer userId = (Integer) session.getAttribute("userId");
	    UserDto user = userService.getUserById(userId);

	    model.addAttribute("targetCalories", CalorieCalculator.calculateTargetCalories(user));

	    return "views/loginpage/inputdata2";
	}

	
	@PostMapping("/updateinputdata2")
	public String updateInputData2(@ModelAttribute UserDto userDto, HttpSession session) {
		Integer userId = (Integer) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/login";
		}
		userDto.setId(userId);
		
		Map<String, int[]> presetRatios = Map.of(
			"common", new int[]{57, 18, 25},
			"health", new int[]{50, 30, 20},
			"quito", new int[]{60, 15, 25},
			"vegan", new int[]{8, 22, 70}
		);
		int[] preset = presetRatios.getOrDefault(userDto.getType(), new int[]{-1, -1, -1});
		System.out.println("현재 타입: " + userDto.getType());
		System.out.println("입력값: " + userDto.getCarbohydrate() + ", " + userDto.getProtein() + ", " + userDto.getFat());
		System.out.println("preset: " + Arrays.toString(preset));

		if (!(preset[0] == userDto.getCarbohydrate() &&
			  preset[1] == userDto.getProtein() &&
			  preset[2] == userDto.getFat())) {
			userDto.setType("custom");
		}
		System.out.println(userId);
		userMapper.updateUserInitInfo2(userDto);
		return "redirect:/friendpage";
	}


}
