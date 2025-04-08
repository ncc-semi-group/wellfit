package com.example.demo.friendship.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.FriendshipDto;
import com.example.demo.dto.FriendshipRequestDto;
import com.example.demo.dto.UserDto;
import com.example.demo.friendship.service.FriendshipRequestService;
import com.example.demo.friendship.service.FriendshipService;
import com.example.demo.user.service.UserPageService;
import com.example.demo.user.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FriendshipController {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final FriendshipRequestService friendshipRequestService;
    private final UserPageService userPageService;
    
    // 친구 페이지
    @GetMapping("/friendpage")
    public String friendList(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<UserDto> friends = friendshipService.getFriendsByUserId(userId);
        System.out.println("친구 목록:");
        for (UserDto friend : friends) {
            // 프로필 이미지 경로 처리
            if (friend.getProfileImage() != null && !friend.getProfileImage().startsWith("/")) {
                friend.setProfileImage("/images/" + friend.getProfileImage());
            }
            System.out.println("ID: " + friend.getId() + ", Nickname: " + friend.getNickname() + ", ProfileImage: " + friend.getProfileImage());
        }
        model.addAttribute("friends", friends);
        
        UserDto userDto = userPageService.getUserProfile(userId);
        model.addAttribute("user", userDto);
        
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
        // 추천 친구 프로필 이미지 경로 처리
        for (UserDto friend : recommandFriends) {
            if (friend.getProfileImage() != null && !friend.getProfileImage().isEmpty()) {
                if (!friend.getProfileImage().startsWith("/")) {
                    friend.setProfileImage("/images/" + friend.getProfileImage());
                }
            }
        }

        List<FriendshipRequestDto> requests = friendshipRequestService.getFriendRequestsByUserId(userId);
        System.out.println("Total requests found: " + requests.size());
        
        Map<Integer, UserDto> senderMap = new HashMap<>();
        for (FriendshipRequestDto request : requests) {
            int senderId = request.getSenderId();
            System.out.println("Processing request ID: " + request.getId() + ", sender ID: " + senderId);
            
            if (senderId == 0) {
                System.out.println("Warning: Invalid sender ID (0) found for request ID: " + request.getId());
                continue;
            }
            
            if (!senderMap.containsKey(senderId)) {
                UserDto sender = userService.getUserById(senderId);
                System.out.println("Sender info: " + (sender != null ? "found" : "not found"));
                if (sender != null) {
                    // 친구 요청 보낸 사용자의 프로필 이미지 경로 처리
                    System.out.println("Original profile image: " + sender.getProfileImage());
                    if (sender.getProfileImage() != null && !sender.getProfileImage().isEmpty()) {
                        if (!sender.getProfileImage().startsWith("/")) {
                            sender.setProfileImage("/images/" + sender.getProfileImage());
                        }
                    }
                    System.out.println("Processed profile image: " + sender.getProfileImage());
                    senderMap.put(senderId, sender);
                    System.out.println("Added sender to map with nickname: " + sender.getNickname() + ", profile: " + sender.getProfileImage());
                }
            }
        }
        
        UserDto userDto = userPageService.getUserProfile(userId);
        // 현재 사용자의 프로필 이미지 경로 처리
        if (userDto.getProfileImage() != null && !userDto.getProfileImage().isEmpty()) {
            if (!userDto.getProfileImage().startsWith("/")) {
                userDto.setProfileImage("/images/" + userDto.getProfileImage());
            }
        }
        
        model.addAttribute("user", userDto);
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

    // 특정 유저의 친구 목록 페이지
    @GetMapping("/userpage/friends/{userId}")
    public String userFriendsList(@PathVariable("userId") int userId, HttpSession session, Model model) {
        Integer currentUserId = (Integer) session.getAttribute("userId");
        if (currentUserId == null) {
            return "redirect:/login";
        }

        // 해당 유저의 친구 목록 조회
        List<UserDto> friends = friendshipService.getFriendsByUserId(userId);
        System.out.println("친구 목록:");
        for (UserDto friend : friends) {
            // 프로필 이미지 경로 처리
            if (friend.getProfileImage() != null && !friend.getProfileImage().startsWith("/")) {
                friend.setProfileImage("/images/" + friend.getProfileImage());
            }
            System.out.println("ID: " + friend.getId() + ", Nickname: " + friend.getNickname() + ", ProfileImage: " + friend.getProfileImage());
        }
        
        // 해당 유저의 정보 조회
        UserDto userDto = userPageService.getUserProfile(userId);
        if (userDto.getProfileImage() != null && !userDto.getProfileImage().startsWith("/")) {
            userDto.setProfileImage("/images/" + userDto.getProfileImage());
        }
        
        model.addAttribute("user", userDto);
        model.addAttribute("friends", friends);
        model.addAttribute("showHeader", false);
        model.addAttribute("showFooter", false);
        
        return "views/friendpage/userfriendpage";
    }
}

