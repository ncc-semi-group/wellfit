package com.example.demo.chat.service;

import com.example.demo.dto.chat.Chat;
import com.example.demo.dto.chat.Chatroom;
import com.example.demo.dto.chat.ChatroomUser;
import com.example.demo.dto.chat.MessageType;
import com.example.demo.chat.dto.*;
import com.example.demo.chat.mapper.ChatMapper;
import com.example.demo.chat.mapper.ChatroomMapper;
import com.example.demo.chat.mapper.ChatroomUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMapper chatMapper;
    private final ChatroomMapper chatroomMapper;
    private final ChatroomUserMapper chatroomUserMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatRedisService chatRedisService;

    public Long createChatroom(ChatroomCreateDto dto){
        Chatroom chatroom = dto.toChatroom();
        log.info("chatroom : {}", chatroom);
        chatroomMapper.insertChatroom(chatroom);
        return chatroom.getId();
    }
    @Transactional
    public void createChatroomUser(ChatRequestDto chatDto){
        log.info(chatDto.toString());
        if(!chatroomMapper.isFull(chatDto.getRoomId())){
            Chat chat = chatDto.toEntity(MessageType.CREATE);
            System.out.println("chat = " + chat);
            chatroomUserMapper.insertChatroomUser(chat.getChatroomId(), chat.getUserId());
            simpMessagingTemplate.convertAndSend("/sub/chatroom/" + chat.getChatroomId(), chat.toResponseDto());
            createChat(chat);
        }
    }
    public void talk(ChatRequestDto chatDto) {
//        if(chatroomUserMapper.findChatroomUserByIds(Long.parseLong(chatDto.getRoomId()), Long.parseLong(chatDto.getUserId())) == null){
//            throw new RuntimeException("채팅방에 존재하지 않는 유저입니다.");
//        }
        Chat chat = chatDto.toEntity(MessageType.TALK);
        simpMessagingTemplate.convertAndSend("/sub/chatroom/" + chat.getChatroomId(), chat.toResponseDto());
        chatMapper.insertChat(chat);
    }
    public void onChatroom(ChatEnterDto enterDto){
        simpMessagingTemplate.convertAndSend("/sub/chatroom/" + enterDto.getRoomId(), enterDto);
        chatroomUserMapper.updateActive(enterDto.getUserId(), enterDto.getRoomId());
        chatroomUserMapper.updateLatestReadTime(enterDto.getRoomId(), enterDto.getUserId());
    }
    public void offChatroom(ChatExitDto exitDto){
        simpMessagingTemplate.convertAndSend("/sub/chatroom/" + exitDto.getRoomId(), exitDto);
        chatroomUserMapper.updateActive(exitDto.getUserId(), exitDto.getRoomId());
        chatroomUserMapper.updateLatestReadTime(exitDto.getRoomId(), exitDto.getUserId());
    }
    public void exitChatRoom(ChatRequestDto chatDto){
        Chat chat = chatDto.toEntity(MessageType.DELETE);
        simpMessagingTemplate.convertAndSend("/sub/chatroom/" + chat.getChatroomId(), chat.toResponseDto());
        createChat(chat);
        chatroomUserMapper.deleteChatroomUser(chat.getChatroomId(), chat.getUserId());
        if(chatroomUserMapper.countChatroomUsers(chat.getChatroomId()) == 0){
            chatroomMapper.deleteChatroom(chat.getChatroomId());
        }
    }
    // Chat CRUD
    public void createChat(Chat chat){
        chatMapper.insertChat(chat);
    }
    public List<ChatroomDto> findChatroomList(Long userId){
        log.info("userId : {}", userId);
        return chatroomMapper.findChatroomList(userId);
    }
    public List<ChatResponseDto> findChatByRoomId(Long roomId){
        return chatMapper.findChatByChatroomId(roomId).stream().map(c -> c.toResponseDto()).toList();
    }
    public List<ChatResponseDto> findChatByRoomIdAndUserId(Long roomId, Long userId){
        return chatMapper.findChatByChatroomIdAndUserId(roomId, userId).stream().map(c -> c.toResponseDto()).toList();
    }
    // ChatroomUser CRUD
    public ChatroomUser findChatroomUserIds(Long roomId, Long userId){
        return chatroomUserMapper.findChatroomUserByIds(roomId, userId);
    }
    public List<ChatroomUserDto> findChatroomUserByChatroomId(Long roomId){
        return chatroomUserMapper.findChatroomUserByChatroomId(roomId);
    }

    public void updateActive(Long userId, Long roomId){
        chatroomUserMapper.updateActive(userId, roomId);
    }
    // Chatroom CRUD
    public void deleteChatRoom(Long roomId){
        chatroomMapper.deleteChatroom(roomId);
    }
    public ChatroomDto findChatroomByRoomId(Long roomId){
        return chatroomMapper.findChatroomById(roomId);
    }

    public List<ChatroomDto> findChatroomByUserId(Long userId) {
        return chatroomMapper.findChatroomByUserId(userId)
                .stream().map(c -> {
                    c.setLatestMessage(chatMapper.findLatestMessageByChatroomId(c.getRoomId()));
                    c.setUserCount(chatroomUserMapper.getChatroomUserCount(c.getRoomId()));
                    return c;
                }).toList();
    }

    public void read(ChatRequestDto chatDto) {
        log.info("read : {}", chatDto);
        Chat chat = chatDto.toEntity(MessageType.READ);
        simpMessagingTemplate.convertAndSend("/sub/chatroom/" + chat.getChatroomId(), chat.toResponseDto());
    }

    public void image(ChatRequestDto chatDto) {
        Chat chat = chatDto.toEntity(MessageType.IMAGE);
        simpMessagingTemplate.convertAndSend("/sub/chatroom/" + chat.getChatroomId(), chat.toResponseDto());
        chatMapper.insertChat(chat);
    }
}
