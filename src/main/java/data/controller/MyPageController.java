package data.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import data.dto.BadgeDto;
import data.dto.BoardDto;
import data.dto.CommentDto;
import data.dto.UserBadgeDto;
import data.dto.UserDto;
import data.service.BadgeService;
import data.service.BoardLikeService;
import data.service.BoardService;
import data.service.CommentService;
import data.service.UserBadgeService;
import data.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

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
	
	@GetMapping("/mypage")
	public String goMypage(HttpSession session, Model model) {
		//테스트용
		int id = 1;
		
		UserDto dto = userService.getSelectUser(id);
			
		List<UserBadgeDto> badgeList = userBadgeService.getSelectUserId(id);
			
		int badgeCount = (int) badgeList.stream()
				.filter(badge -> badge.getUserBadgeId() != 0 && badge.getIsAchieved() == 1)
				.count();
		dto.setBadgeCount(badgeCount);
	
		// 좋아요 목록 가져오기 (태그별로 그룹화)
		List<Map<String, Object>> likeList = boardLikeService.getLikesByTag(id);
		model.addAttribute("dto", dto);
		model.addAttribute("likeList", likeList);
		
		return "views/mypage/mypage";
	}
	
	@GetMapping("/mypageupdate")
	public String updateForm(HttpSession session, Model model) {
		//테스트용
		int id = 1;
		
		UserDto dto = userService.getSelectUser(id);
		model.addAttribute("dto", dto);
		
		return "views/mypage/mypageupdate";
	}
	
	@PostMapping("/update")
	public String updateUser(@ModelAttribute UserDto dto, 
						   @RequestParam(value = "upload", required = false) MultipartFile upload) {
		try {
			// 프로필 이미지 처리
			if (upload != null && !upload.isEmpty()) {
				String originalFileName = upload.getOriginalFilename();
				String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
				String newFileName = UUID.randomUUID().toString() + fileExtension;
				
				// 업로드 디렉토리가 없으면 생성
				File directory = new File(uploadDir);
				if (!directory.exists()) {
					directory.mkdirs();
				}
				
				// 파일 저장
				File destFile = new File(uploadDir + File.separator + newFileName);
				upload.transferTo(destFile);
				
				// 프로필 이미지 경로 설정
				dto.setProfileImage(newFileName);
			}
			
			// 사용자 정보 업데이트
			userService.mypageUpdateUser(dto);
			
			return "redirect:/mypage";
		} catch (IOException e) {
			e.printStackTrace();
			return "redirect:/mypageupdate?error";
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
	    
	    return "views/mypage/mypageComment";
	}
	
    @GetMapping("/mypageLike")
    public String mypageLike(@RequestParam("tag") String tag, Model model) {
        //테스트용
        int userId = 1;
        
        // 해당 태그의 좋아요 게시물 목록 조회
        List<BoardDto> posts = boardLikeService.getLikedBoardsByTag(userId, tag);
        
        model.addAttribute("tag", tag);
        model.addAttribute("posts", posts);
        
        return "views/mypage/mypageLike";
    }
}
