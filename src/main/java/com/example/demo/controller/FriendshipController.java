package com.example.demo.controller;

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

import com.example.demo.dto.FriendshipDto;
import com.example.demo.dto.FriendshipRequestDto;
import com.example.demo.dto.UserDto;
import com.example.demo.service.FriendshipRequestService;
import com.example.demo.service.FriendshipService;
import com.example.demo.service.UserService;

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
            return "redirect:/login";
        }

        List<UserDto> friends = friendshipService.getFriendsByUserId(userId);
        model.addAttribute("friends", friends);
        
        return "views/friendpage/friendpage";
    }

    // 친구 요청/수락 페이지
    @GetMapping("/friends_request")
    public String friendRequest(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<UserDto> recommandFriends = friendshipRequestService.getRecommendFriends(userId);
        List<FriendshipRequestDto> requests = friendshipRequestService.getFriendRequestsByUserId(userId);
        
        Map<Integer, UserDto> senderMap = new HashMap<>();
        for (FriendshipRequestDto request : requests) {
            int senderId = request.getSenderId();
            if (!senderMap.containsKey(senderId)) {
                UserDto sender = userService.getUserById(senderId);
                senderMap.put(senderId, sender);
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
        return ResponseEntity.ok(userList);
    }
    
    // 친구 요청 수락
    @PostMapping("/accept")
    public ResponseEntity<String> acceptFriendRequest(@RequestBody FriendshipRequestDto dto) {
        friendshipService.acceptFriendRequest(dto.getId(), dto.getUserId(), dto.getSenderId());
        return ResponseEntity.ok("친구 요청을 수락했습니다.");
    }

    // 친구 요청 거절
    @PostMapping("/reject")
    public ResponseEntity<String> rejectFriendRequest(@RequestBody FriendshipRequestDto dto) {
        friendshipRequestService.rejectFriendRequest(dto.getId());
        return ResponseEntity.ok("친구 요청을 거절했습니다.");
    }
}
