package com.example.demo.chat.mapper;

import com.example.demo.dto.chat.Chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface ChatMapper {
    void insertChat(Chat chat);
    List<Chat> findChatByChatroomId(Long roomId);
    List<Chat> findChatByChatroomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);
    String findLatestMessageByChatroomId(@Param("roomId") Long roomId);
    int findUnreadChatCountByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("latestReadTime") Timestamp latestReadTime);
}
