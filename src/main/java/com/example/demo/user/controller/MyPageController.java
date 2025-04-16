package com.example.demo.user.controller;

import com.example.demo.badge.service.BadgeService;
import com.example.demo.badge.service.UserBadgeService;
import com.example.demo.board.service.BoardLikeService;
import com.example.demo.board.service.BoardService;
import com.example.demo.board.service.CommentService;
import com.example.demo.dto.board.BoardDto;
import com.example.demo.dto.board.BoardLikeDto;
import com.example.demo.dto.board.CommentDto;
import com.example.demo.dto.user.BadgeDto;
import com.example.demo.dto.user.UserBadgeDto;
import com.example.demo.dto.user.UserDto;
import com.example.demo.ncp.storage.NcpObjectStorageService;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MyPageController {
	final UserService userService;
	final UserBadgeService userBadgeService;
	final BadgeService badgeService;
	final BoardService boardService;
	final CommentService commentService;
	final BoardLikeService boardLikeService;
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Autowired
	private NcpObjectStorageService ncpObjectStorageService;
	
	@GetMapping("/mypage")
	public String goMypage(HttpSession session, Model model) {
		// 세션에서 로그인한 사용자의 ID를 가져옴
		Integer userId = (Integer) session.getAttribute("userId");
		
		// 로그인하지 않은 경우 로그인 페이지로 리다이렉트
		if (userId == null) {
			return "redirect:/loginpage";
		}
		
		// 사용자 정보와 좋아요 목록을 가져옴
		UserDto userDto = userService.getSelectUser(userId);
		List<BoardLikeDto> likeList = boardLikeService.getLikesByTag(userId);
		if (userDto != null) {
			model.addAttribute("dto", userDto);
			model.addAttribute("likeList", likeList);
			model.addAttribute("showHeader", false);
			
			return "views/mypage/mypage";
		} else {
			return "redirect:/loginpage";
		}
	}
	
	@GetMapping("/mypageupdate")
	public String updateForm(HttpSession session, Model model) {
		// 세션에서 로그인한 사용자의 ID를 가져옴
		Integer userId = (Integer) session.getAttribute("userId");
		
		// 로그인하지 않은 경우 로그인 페이지로 리다이렉트
		if (userId == null) {
			return "redirect:/login";
		}
		
		UserDto dto = userService.getSelectUser(userId);
		if (dto != null) {
			model.addAttribute("dto", dto);
			model.addAttribute("showHeader", false);
			return "views/mypage/mypageUpdate";
		} else {
			return "redirect:/login";
		}
	}
	
	@PostMapping("/update")
	public String updateUser(@ModelAttribute UserDto dto, 
						   @RequestParam(value = "file", required = false) MultipartFile file) {
		try {
			// 프로필 이미지 처리
			if (file != null && !file.isEmpty()) {
				// 네이버 클라우드 스토리지에 업로드
				String imageUrl = ncpObjectStorageService.uploadImage(file);
				dto.setProfileImage(imageUrl);
			}
			
			// 사용자 정보 업데이트
			userService.mypageUpdateUser(dto);
			
			return "redirect:/mypage";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/mypageUpdate?error";
		}
	}
	
	@GetMapping("/mypageBadge")
	public String badgeList(@RequestParam("id") int userId, Model model) {
		// 전체 뱃지 목록 가져오기
		List<BadgeDto> allBadges = badgeService.getAllBadges();
		
		// 사용자의 뱃지 현황 가져오기
		List<UserBadgeDto> userBadges = userBadgeService.getSelectUserId(userId);
		
		// 뱃지 정보 합치기
		for (BadgeDto badge : allBadges) {
			// 사용자가 보유한 뱃지인지 확인
			UserBadgeDto userBadge = userBadges.stream()
				.filter(ub -> ub.getBadgeId() == badge.getId())
				.findFirst()
				.orElse(null);
			
			if (userBadge != null) {
				badge.setIsAchieved(userBadge.getIsAchieved());
				badge.setCondition_count(userBadge.getCondition_count());
			} else {
				badge.setIsAchieved(0);
				badge.setCondition_count(0);
			}
		}
		
		model.addAttribute("badgeList", allBadges);
		model.addAttribute("showHeader", false);
		return "views/mypage/mypageBadge";
	}
	
	
	@GetMapping("/mypageActive")
	public String getMyBoards(@RequestParam(value="id", required = false) int id, Model model, HttpSession session) {
		// 세션에서 현재 로그인한 사용자 ID 가져오기
		Integer userId = (Integer) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/login";  // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
		}

		// 사용자의 게시물 목록 조회
		List<BoardDto> boardList = boardService.getSelectUserId(userId);
		model.addAttribute("boardList", boardList);
		model.addAttribute("showHeader", false);
		
		return "views/mypage/mypageActive";
	}
	
	@GetMapping("/mypageComment")
	public String getMyComments(@RequestParam(value = "id",required=false) int id, Model model, HttpSession session) {
	    // 세션에서 현재 로그인한 사용자 ID 가져오기
	    Integer userId = (Integer) session.getAttribute("userId");
	    if (userId == null) {
	        return "redirect:/login";  // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
	    }

	    // 사용자의 댓글 목록 조회
	    List<CommentDto> commentList = commentService.getSelectUserId(userId);
	    model.addAttribute("commentList", commentList);
		model.addAttribute("showHeader", false);
	    
	    return "views/mypage/mypageComment";
	}
	
    @GetMapping("/mypageLike")
    public String mypageLike(@RequestParam("tag") String tag, Model model, HttpSession session) {
        // 세션에서 로그인한 사용자의 ID를 가져옴
        Integer userId = (Integer) session.getAttribute("userId");
        
        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (userId == null) {
            return "redirect:/login";
        }
        
        // 해당 태그의 좋아요 게시물 목록 조회
        List<BoardDto> posts = boardLikeService.getLikedBoardsByTag(userId, tag);
        
        model.addAttribute("tag", tag);
        model.addAttribute("posts", posts);
        model.addAttribute("showHeader", false);
        
        return "views/mypage/mypageLike";
    }
	@GetMapping("/user/detail/{userId}")
	public ResponseEntity<?> getUserDetail(@PathVariable("userId") int userId) {
		UserDto userDto = userService.getSelectUser(userId);
		if (userDto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(userDto);
	}

    @GetMapping("/follower/{userId}")
    public String followerList(@PathVariable int userId, Model model) {
        UserDto user = userService.getUserProfile(userId);
        List<UserDto> followers = userService.getUserFollowers(userId);
        model.addAttribute("user", user);
        model.addAttribute("followers", followers);
        return "views/mypage/followerpage";
    }

    @GetMapping("/following/{userId}")
    public String followingList(@PathVariable int userId, Model model) {
        UserDto user = userService.getUserProfile(userId);
        List<UserDto> following = userService.getUserFollowing(userId);
        model.addAttribute("user", user);
        model.addAttribute("following", following);
        return "views/mypage/followingpage";
    }

}