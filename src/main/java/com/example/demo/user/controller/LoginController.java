package com.example.demo.user.controller;

import java.awt.Window;
import java.util.Arrays;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.user.UserDto;
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

	@PostMapping("/api/loginpage/logincheck")
	@ResponseBody
	public ResponseEntity<String> loginCheck(@RequestParam("email") String email,
	                                         @RequestParam("password") String password,
	                                         HttpSession session) {

	    UserDto user = userService.login(email, password);

	    if (user == null) {
	        return ResponseEntity.status(401).body("아이디 또는 비밀번호가 일치하지 않습니다.");
	    }

	    session.setAttribute("userId", user.getId());
	    return ResponseEntity.ok("로그인 성공");
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

		userMapper.updateUserInitInfo2(userDto);
		return "redirect:/friendpage";
	}

	// 로그인 상태 검증 API (Ajax용)
	@PostMapping("/api/verification")
	@ResponseBody
	public String verifyLogin(HttpSession session) {
		Integer userId = (Integer) session.getAttribute("userId");
		if (userId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
		}
		return "ok";
	}
}
