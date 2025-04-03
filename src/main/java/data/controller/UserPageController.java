package data.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import data.dto.UserDto;
import data.service.UserPageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserPageController {
    private final UserPageService userPageService;

    @GetMapping("/userpage/{userId}")
    public String userPage(@PathVariable("userId") int userId, Model model, HttpSession session) {
        // 현재 로그인한 사용자 확인
        Integer loginUserId = (Integer) session.getAttribute("userId");
        
        // 본인 프로필을 조회하려는 경우 마이페이지로 리다이렉트
        if (loginUserId != null && loginUserId == userId) {
            return "redirect:/mypage";
        }

        UserDto userDto = userPageService.getUserProfile(userId);
        model.addAttribute("user", userDto);
        
        return "views/userpage/userpage";
    }
    
    @GetMapping("/users")
    public String userList(Model model, HttpSession session) {
        Integer loginUserId = (Integer) session.getAttribute("userId");
        if (loginUserId == null) {
            return "redirect:/login";
        }
        
        List<UserDto> otherUsers = userPageService.getOtherUsers(loginUserId);
        model.addAttribute("users", otherUsers);
        
        return "views/userpage/userlist";
    }
}