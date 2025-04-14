package com.example.demo.friendship.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.friendship.FriendshipRequestDto;
import com.example.demo.dto.user.UserDto;
import com.example.demo.friendship.service.FriendshipRequestService;
import com.example.demo.friendship.service.FriendshipService;
import com.example.demo.user.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FriendshipController {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final FriendshipRequestService friendshipRequestService;

    // 친구 페이지
    @GetMapping("/friendpage")
    public String friendList(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            System.out.println("세션에서 userId가 없습니다. 로그인 페이지로 리다이렉트");
            return "redirect:/loginpage";
        }

        List<UserDto> friends = friendshipService.getFriendsByUserId(userId);
        model.addAttribute("friends", friends);
        System.out.println("친구 목록: " + friends);
        
        return "views/friendpage/friendpage";
    }

    // 친구 요청/수락 페이지
 // 친구 요청 페이지에서 송신자 ID가 0인 경우를 처리
    @GetMapping("/friendrequestpage")
    public String friendRequest(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            System.out.println("세션에서 userId가 없습니다. 로그인 페이지로 리다이렉트");
            return "redirect:/loginpage";
        }

        List<UserDto> recommandFriends = friendshipRequestService.getRecommendFriends(userId);
        System.out.println("추천 친구 목록: " + recommandFriends);

        List<FriendshipRequestDto> requests = friendshipRequestService.getFriendRequestsByUserId(userId);
        System.out.println("친구 요청 목록: " + requests);
        
        Map<Integer, UserDto> senderMap = new HashMap<>();
        for (FriendshipRequestDto request : requests) {
            int senderId = request.getSenderId();
            if (senderId == 0) {
                System.out.println("유효하지 않은 송신자 ID: " + senderId);
                continue; // senderId가 0이면 무시
            }
            System.out.println("송신자 ID: " + senderId);
            if (!senderMap.containsKey(senderId)) {
                UserDto sender = userService.getUserById(senderId);
                if (sender == null) {
                    System.out.println("senderId " + senderId + "에 해당하는 유저를 찾을 수 없습니다.");
                } else {
                    senderMap.put(senderId, sender);
                }
            }
        }
        
        model.addAttribute("recommandFriends", recommandFriends);
        model.addAttribute("requests", requests);
        model.addAttribute("senderMap", senderMap);
        model.addAttribute("showHeader", false);
        model.addAttribute("showFooter", false);
        
        return "views/friendpage/friendrequestpage";
    }

    
    // 친구 검색
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchFriends(@RequestParam("nickname") String nickname) {
        System.out.println("검색 요청 nickname: " + nickname);
        List<UserDto> userList = userService.searchUsersByNickname(nickname);
        System.out.println("검색 결과: " + userList);
        return ResponseEntity.ok(userList);
    }
    
    // 친구 요청 수락
    @PostMapping("/accept")
    public ResponseEntity<String> acceptFriendRequest(@RequestBody FriendshipRequestDto dto) {
        try {
            System.out.println("친구 요청 수락 ID: " + dto.getId() + ", 사용자 ID: " + dto.getUserId() + ", 송신자 ID: " + dto.getSenderId());
            friendshipService.acceptFriendRequest(dto.getId(), dto.getUserId(), dto.getSenderId());
            System.out.println("친구 요청을 수락했습니다.");
            return ResponseEntity.ok("친구 요청을 수락했습니다.");
        } catch (Exception e) {
            System.out.println("친구 요청 수락 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("친구 요청 수락 중 오류가 발생했습니다.");
        }
    }

    // 친구 요청 거절
    @PostMapping("/reject")
    public ResponseEntity<String> rejectFriendRequest(@RequestBody FriendshipRequestDto dto) {
        try {
            System.out.println("친구 요청 거절 ID: " + dto.getId());
            friendshipRequestService.rejectFriendRequest(dto.getId());
            System.out.println("친구 요청을 거절했습니다.");
            return ResponseEntity.ok("친구 요청을 거절했습니다.");
        } catch (Exception e) {
            System.out.println("친구 요청 거절 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("친구 요청 거절 중 오류가 발생했습니다.");
        }
    }
}
