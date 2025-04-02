package data.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import data.dto.BadgeDto;
import data.dto.UserBadgeDto;
import data.dto.UserDto;
import data.service.BadgeService;
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
	
		// 좋아요 목록 가져오기
		List<UserDto> likeList = userService.getLikeList(id);
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
}
