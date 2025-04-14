package com.example.demo.chat.controller;

import com.example.demo.chat.dto.*;
import com.example.demo.chat.service.ChatRedisService;
import com.example.demo.chat.service.ChatService;
import com.example.demo.ncp.storage.NcpObjectStorageService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class ChatController {
    private final ChatService chatService;
    private final ChatRedisService redis;
    private final NcpObjectStorageService ncpObjectStorageService;
    private String bucketName = "wellfit";
    public ChatController(ChatService chatService, ChatRedisService redis, NcpObjectStorageService ncpObjectStorageService) {
        this.chatService = chatService;
        this.redis = redis;
        this.ncpObjectStorageService = ncpObjectStorageService;
    }

    // Test
    @GetMapping("/chat/test")
    public String test(){
        return "chat/test";
    }
    // Template
    @GetMapping("/chat")
    public String chatroomList(HttpSession session, Model model) {
        Long userId = ((Integer)session.getAttribute("userId")).longValue();
        if(userId == null) {
            return "redirect:/loginpage"; // 로그인 페이지로 리다이렉트
        }
        // 채팅방 목록을 가져와서 모델에 추가
        chatService.findChatroomList(userId);
        model.addAttribute("userId", userId);
        model.addAttribute("showHeader", false);
        return "chat/chatroomList";
    }
    @GetMapping("/chat/list/all")
    @ResponseBody
    public ResponseEntity<?> chatroomList(HttpSession session) {
        try {
            Long userId = ((Integer)session.getAttribute("userId")).longValue();
            return ResponseEntity.ok(chatService.findChatroomList(userId));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }
    @GetMapping("/chat/list/my")
    @ResponseBody
    public ResponseEntity<?> userChatroomList(HttpSession session) {
        try {
            Long userId = ((Integer)session.getAttribute("userId")).longValue();
            return ResponseEntity.ok(chatService.findChatroomByUserId(userId));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }
    @GetMapping("/chatroom/form")
    public String chatroomForm(Model model, HttpSession session) {
        Long userId = ((Integer)session.getAttribute("userId")).longValue();
        model.addAttribute("userId", userId);
        if(userId == null) {
            return "redirect:/loginpage"; // 로그인 페이지로 리다이렉트
        }
        return "/chat/chatroomForm";
    }

    @GetMapping("/chatroom/enter/{roomId}")
    public String chatroom(Model model,
                           @PathVariable Long roomId,
                           HttpSession session) {
        Long userId = ((Integer)session.getAttribute("userId")).longValue();
        if(userId == null) {
            return "redirect:/loginpage"; // 로그인 페이지로 리다이렉트
        }
        model.addAttribute("users", chatService.findChatroomUserByChatroomId(roomId));
        model.addAttribute("userId", userId);
        model.addAttribute("roomId", roomId);
        return "chat/chatroom"; // Thymeleaf 템플릿
    }

    @MessageMapping("/chat/off")
    public void exit(ChatExitDto exitDto) {
        // Redis에 최신 읽기 시간 업데이트
        redis.updateLatestReadTime(exitDto.getRoomId(), exitDto.getUserId(), exitDto.getCreatedAt());
        // 현재 유저의 퇴장 메시지 채팅 전송, DB에 저장
        chatService.offChatroom(exitDto);
        log.info("User EXIT");
    }

    @MessageMapping("/chat/on")
    public void enter(ChatEnterDto enterDto) {
        // 현재 채팅방에 메시지 전송, DB에 저장
        chatService.onChatroom(enterDto);
        // Redis에 최신 읽기 시간 업데이트
        redis.updateLatestReadTime(enterDto.getRoomId(), enterDto.getUserId(), enterDto.getCreatedAt());
        log.info("User ENTER");
    }

    @MessageMapping("/chat/talk")
    public void talk(ChatRequestDto chatDto){
        // 현재 채팅방에 메시지 전송, DB에 저장
        chatService.talk(chatDto);
        // Redis에 최신 읽기 시간 업데이트
        redis.updateLatestReadTime(chatDto.getRoomId(), chatDto.getUserId(), chatDto.getCreatedAt());
    }
    @MessageMapping("/chat/read")
    public void read(ChatRequestDto chatDto) {
        // Redis에 최신 읽기 시간 업데이트
        redis.updateLatestReadTime(chatDto.getRoomId(), chatDto.getUserId(), chatDto.getCreatedAt());
        chatService.read(chatDto);
    }
    @MessageMapping("/chat/image")
    public void image(ChatRequestDto chatDto){
        // 현재 채팅방에 이미지 전송, DB에 저장
        chatService.image(chatDto);
        // Redis에 최신 읽기 시간 업데이트
        redis.updateLatestReadTime(chatDto.getRoomId(), chatDto.getUserId(), chatDto.getCreatedAt());
    }
    @PostMapping("/chat/upload/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 파일이 존재하면 S3에 업로드
            if (file != null && !file.isEmpty()) {
                log.info("File uploaded: " + file.getOriginalFilename());
                log.info("File size: " + file.getSize() + " bytes");
                String fileUrl = ncpObjectStorageService.uploadFile(bucketName,"image/chat" ,file);
                log.info("File URL: " + fileUrl);
                Map<String, String> response = new HashMap<>();
                response.put("url", fileUrl);
                return ResponseEntity.ok(response); // 업로드된 이미지 URL을 반환
            } else {
                return ResponseEntity.badRequest().body("No file uploaded");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }
    @PostMapping("/chatroom/create")
    public String createChatroom(HttpSession session ,@ModelAttribute ChatroomCreateDto dto,@RequestParam(value = "chatroomImage", required = false) MultipartFile file){
        try{
            Long userId = ((Integer)session.getAttribute("userId")).longValue();
            dto.setUserId(userId);

            // 파일이 존재하면 S3에 업로드
            if (file != null && !file.isEmpty()) {
                log.info("File uploaded: " + file.getOriginalFilename());
                log.info("File size: " + file.getSize() + " bytes");
                String fileUrl = ncpObjectStorageService.uploadFile(bucketName,"image/chatroom" ,file);
                log.info("File URL: " + fileUrl);
                dto.setImageUrl(fileUrl); // 업로드된 이미지 URL을 DTO에 설정
            }else{
                log.info("No file uploaded, using default image.");
                dto.setImageUrl("https://bucketcamp148.s3.ap-northeast-2.amazonaws.com/image/chatroom/default.png");
            }

            Long roomId = chatService.createChatroom(dto);
            System.out.println("userId = " + userId);
            System.out.println("roomId = " + roomId);
            System.out.println("chatroomImage = " + dto.getImageUrl() + ", chatroomName = " + dto.getImageUrl());
            chatService.createChatroomUser(ChatRequestDto.builder().userId(dto.getUserId()).roomId(roomId).createdAt(new Timestamp(System.currentTimeMillis())).build());
            return "redirect:/chat";
        }catch (RuntimeException e){
            e.printStackTrace();
            return "redirect:/chatroom/form";
        }
    }
    @GetMapping("/chatroom/{chatRoomId}/chats")
    @ResponseBody
    public ResponseEntity<?> getChatting(@PathVariable(value = "chatRoomId") Long chatRoomId, @RequestParam(value = "userId") Long userId) {
        try {
            // 전체
            // return ResponseEntity.ok(chatService.findChatByRoomId(chatRoomId));
            // 유저별 채팅
            return ResponseEntity.ok(chatService.findChatByRoomIdAndUserId(chatRoomId, userId));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }
    @PostMapping("/chatroom/exit")
    public ResponseEntity<?> exitChatroom(@RequestBody ChatRequestDto chatDto) {
        try {
            chatService.exitChatRoom(chatDto);
            return ResponseEntity.ok("success");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }
    @GetMapping("/chatroom/{roomId}/detail")
    public ResponseEntity<?> roomDetail(@PathVariable Long roomId){
        try{
            return ResponseEntity.ok(chatService.findChatroomByRoomId(roomId));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }
    @PostMapping("/chatroom/enter")
    @ResponseBody
    public ResponseEntity<?> enterChatRoom(@RequestBody ChatRequestDto chatDto) {
        try {
            chatService.createChatroomUser(chatDto);
            return ResponseEntity.ok("success");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }

    @PostMapping("/chatroom/talk/{roomId}")
    public ResponseEntity<?> talk(@PathVariable(value = "roomId") Long roomId, @RequestBody ChatRequestDto chatDto) {
        try {
            return ResponseEntity.ok("success");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }
    @GetMapping("/chatroom/{roomId}/members")
    @ResponseBody
    public ResponseEntity<?> getChatroomMembers(@PathVariable(value = "roomId") Long roomId) {
        try {
            // 채팅방 사용자 목록 가져오기
            List<ChatroomUserDto> chatroomUsers = chatService.findChatroomUserByChatroomId(roomId);

            // 각 사용자에 대해 최신 읽기 시간을 Redis에서 가져오고 없으면 DB에서 가져옴
            for (ChatroomUserDto userDto : chatroomUsers) {
                Timestamp latestReadTime = redis.getLatestReadTime(userDto.getRoomId(), userDto.getUserId());
                if (latestReadTime == null) {
                    // Redis에 없으면 DB에서 조회
                    latestReadTime = userDto.getLatestReadTime();
                    // DB에서 가져온 값은 Redis에 저장하여 추후 빠르게 접근 가능하도록 함
                    if (latestReadTime != null) {
                        redis.updateLatestReadTime(userDto.getRoomId(), userDto.getUserId(), latestReadTime);
                    }
                }
                // 최신 읽기 시간을 DTO에 설정 (선택 사항)
                userDto.setLatestReadTime(latestReadTime);
            }

            return ResponseEntity.ok(chatroomUsers);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }

}
