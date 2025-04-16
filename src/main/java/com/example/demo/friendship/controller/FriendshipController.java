package com.example.demo.friendship.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.friendship.FriendshipRequestDto;
import com.example.demo.dto.user.UserDto;
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
    	model.addAttribute("currentPage", "friendpage");
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            System.out.println("세션에서 userId가 없습니다. 로그인 페이지로 리다이렉트");
            return "redirect:/loginpage";
        }
        
        UserDto user = userService.getUserById(userId);
        model.addAttribute("user", user);

        String userProfileImage = user.getProfileImage();
        model.addAttribute("userProfileImage", userProfileImage);

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
    public ResponseEntity<List<UserDto>> searchFriends(@RequestParam("nickname") String nickname, HttpSession session) {
    	Integer userId = (Integer) session.getAttribute("userId");
        List<UserDto> userList = userService.searchUsersByNickname(nickname, userId);
        System.out.println("검색 결과: " + userList);
        return ResponseEntity.ok(userList);
    }
    
 // 친구 요청 수락
    @PostMapping("/accept")
    public ResponseEntity<Map<String, String>> acceptFriendRequest(@RequestBody FriendshipRequestDto dto) {
        Map<String, String> response = new HashMap<>();
        try {
            System.out.println("친구 요청 수락 ID: " + dto.getId() + ", 사용자 ID: " + dto.getUserId() + ", 송신자 ID: " + dto.getSenderId());
            friendshipService.acceptFriendRequest(dto.getId(), dto.getUserId(), dto.getSenderId());
            System.out.println("친구 요청을 수락했습니다.");
            
            response.put("status", "OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("친구 요청 수락 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            
            response.put("status", "error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 친구 요청 거절
    @PostMapping("/reject")
    public ResponseEntity<Map<String, String>> rejectFriendRequest(@RequestBody FriendshipRequestDto dto) {
        Map<String, String> response = new HashMap<>();
        try {
            System.out.println("친구 요청 거절 ID: " + dto.getId());
            friendshipRequestService.rejectFriendRequest(dto.getId());
            System.out.println("친구 요청을 거절했습니다.");
            
            response.put("status", "OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("친구 요청 거절 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            
            response.put("status", "error");
            return ResponseEntity.status(500).body(response);
        }
    }
    
 // 특정 유저의 친구 목록 페이지
    @GetMapping("/userpage/friends/{userId}")
    public String userFriendsList(@PathVariable("userId") int userId, HttpSession session, Model model) {
        Integer currentUserId = (Integer) session.getAttribute("userId");
        if (currentUserId == null) {
            return "redirect:/loginpage";
        }

        // 해당 유저의 친구 목록 조회
        List<UserDto> friends = friendshipService.getFriendsByUserId(userId);
        System.out.println("친구 목록:");
        for (UserDto friend : friends) {
            // 프로필 이미지 경로 처리
            if (friend.getProfileImage() != null && !friend.getProfileImage().startsWith("http")) {
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

    // 친구 추가 요청
    @PostMapping("/api/friends/add")
    public ResponseEntity<String> addFriend(@RequestBody FriendshipRequestDto dto, HttpSession session) {
        try {
            Integer currentUserId = (Integer) session.getAttribute("userId");
            if (currentUserId == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            System.out.println("친구 추가 요청 - 현재 사용자 ID: " + currentUserId + ", 대상 ID: " + dto.getUserId());
            
            // 이미 친구인지 확인
            if (friendshipService.isFriend(currentUserId, dto.getUserId())) {
                return ResponseEntity.badRequest().body("이미 친구입니다.");
            }

            // 이미 요청을 보냈는지 확인
            List<FriendshipRequestDto> existingRequests = friendshipRequestService.getFriendRequestsByUserId(dto.getUserId());
            for (FriendshipRequestDto request : existingRequests) {
                if (request.getSenderId() == currentUserId) {
                    return ResponseEntity.badRequest().body("이미 친구 요청을 보냈습니다.");
                }
            }

            // 친구 요청 보내기
            FriendshipRequestDto request = new FriendshipRequestDto();
            request.setUserId(dto.getUserId());  // 받는 사람 ID
            request.setSenderId(currentUserId);   // 보내는 사람 ID
            request.setRequestMessage("친구 요청");
            friendshipRequestService.sendFriendRequest(request.getUserId(), request.getSenderId(), request.getRequestMessage());

            return ResponseEntity.ok("친구 요청을 보냈습니다.");
        } catch (Exception e) {
            System.out.println("친구 요청 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("친구 요청 중 오류가 발생했습니다.");
        }
    }

    // 친구 상태 확인
    @GetMapping("/api/friends/check/{targetId}")
    public ResponseEntity<?> checkFriendshipStatus(@PathVariable("targetId") int targetId, HttpSession session) {
        try {
            Integer currentUserId = (Integer) session.getAttribute("userId");
            if (currentUserId == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            boolean isFriend = friendshipService.isFriend(currentUserId, targetId);
            return ResponseEntity.ok().body(isFriend);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("친구 상태 확인 중 오류가 발생했습니다.");
        }
    }

    // 친구 삭제
    @DeleteMapping("/api/friends/delete/{targetId}")
    public ResponseEntity<String> deleteFriend(@PathVariable("targetId") int targetId, HttpSession session) {
        try {
            Integer currentUserId = (Integer) session.getAttribute("userId");
            if (currentUserId == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            friendshipService.deleteFriend(currentUserId, targetId);
            return ResponseEntity.ok("친구가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("친구 삭제 중 오류가 발생했습니다.");
        }
    }
}