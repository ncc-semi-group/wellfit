package com.example.demo.chat.mapper;

import com.example.demo.dto.chat.Chatroom;
import com.example.demo.chat.dto.ChatroomDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatroomMapper {
    Long insertChatroom(Chatroom chatroom);
    void deleteChatroom(Long roomId);
    ChatroomDto findChatroomById(Long roomId);
    void addReadingCount(Long roomId);
    void subtractReadingCount(Long roomId);
    List<ChatroomDto> findChatroomByUserId(Long userId);
    List<ChatroomDto> findChatroomList(Long userId);
    boolean isFull(Long roomId);
    List<Long> findAllRoomIds();
    void clearAllActiveUsers();
}
