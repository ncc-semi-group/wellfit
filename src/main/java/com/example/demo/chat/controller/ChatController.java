package com.example.demo.chat.controller;

import com.example.demo.chat.dto.ChatEnterDto;
import com.example.demo.chat.dto.ChatExitDto;
import com.example.demo.chat.dto.ChatRequestDto;
import com.example.demo.chat.dto.ChatroomCreateDto;
import com.example.demo.chat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@Slf4j
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Test
    @GetMapping("/")
    public String test(){
        return "chat/test";
    }
    // Template
    @GetMapping("/chatroom/list/{userId}")
    public String chatroomList(Model model, @PathVariable(value = "userId") Long userId){
        // 채팅방 목록을 가져와서 모델에 추가
        chatService.findChatroomList(userId);
        model.addAttribute("userId", userId);
        model.addAttribute("showHeader", false);
        return "chat/chatroomList";
    }
    @GetMapping("/chatroom/all/{userId}")
    @ResponseBody
    public ResponseEntity<?> chatroomList(@PathVariable(value = "userId") Long userId) {
        try {
            return ResponseEntity.ok(chatService.findChatroomList(userId));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }
    @GetMapping("/chatroom/my/{userId}")
    @ResponseBody
    public ResponseEntity<?> userChatroomList(@PathVariable(value = "userId") Long userId) {
        try {
            return ResponseEntity.ok(chatService.findChatroomByUserId(userId));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }
    @GetMapping("/chatroom/form")
    public String chatroomForm(Model model){
        model.addAttribute("userId", 2);
        return "/chat/chatroomForm";
    }

    @GetMapping("/chatroom/enter/{roomId}")
    public String chatroom(Model model,
                           @PathVariable Long roomId,
                           @RequestParam Long userId) {
        model.addAttribute("users", chatService.findChatroomUserByChatroomId(roomId));
        model.addAttribute("userId", userId);
        model.addAttribute("roomId", roomId);
        return "chat/chatroom"; // Thymeleaf 템플릿
    }


    @MessageMapping("/chat/off")
    public void exit(ChatExitDto exitDto) {
        // 현재 유저의 퇴장 메시지 채팅 전송, DB에 저장
        chatService.offChatroom(exitDto);
        log.info("User EXIT");
    }

    @MessageMapping("/chat/on")
    public void enter(ChatEnterDto enterDto) {
        // 현재 채팅방에 메시지 전송, DB에 저장
        chatService.onChatroom(enterDto);
        log.info("User ENTER");
    }

    @MessageMapping("/chat/talk")
    public void talk(ChatRequestDto chatDto){
        // 현재 채팅방에 메시지 전송, DB에 저장
        chatService.talk(chatDto);
    }
    @PostMapping("/chatroom/create")
    public String createChatroom(@ModelAttribute ChatroomCreateDto dto,@RequestParam(value = "chatroomImage", required = false) MultipartFile file){
        try{
            Long roomId = chatService.createChatroom(dto);
            System.out.println("roomId = " + roomId);
            chatService.createChatroomUser(ChatRequestDto.builder().userId(dto.getUserId().toString()).roomId(roomId.toString()).createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build());
            return "redirect:/chatroom/list/"+dto.getUserId();
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
    public ResponseEntity<?> getChatroomMembers(@PathVariable(value = "roomId") Long roomId) {
        try {
            return ResponseEntity.ok(chatService.findChatroomUserByChatroomId(roomId));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }
}
