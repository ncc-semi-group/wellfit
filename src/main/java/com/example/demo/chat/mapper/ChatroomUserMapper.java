package com.example.demo.chat.mapper;

import com.example.demo.chat.dto.ChatroomUserDto;
import com.example.demo.dto.chat.ChatroomUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface ChatroomUserMapper {
    void insertChatroomUser(@Param("roomId") Long roomId, @Param("userId") Long userId);
    void deleteChatroomUser(@Param("roomId") Long roomId, @Param("userId") Long userId);
    ChatroomUser findChatroomUserByIds(@Param("roomId") Long roomId, @Param("userId") Long userId);
    List<ChatroomUserDto> findChatroomUserByChatroomId(@Param("roomId") Long roomId);
    List<ChatroomUserDto> findChatroomUserByUserId(@Param("userId") Long userId);
    int countChatroomUsers(@Param("roomId") Long roomId);
    void updateLatestReadTime(@Param("roomId") Long roomId, @Param("userId") Long userId);
    int getChatroomUserCount(@Param("roomId") Long roomId);
    void updateLatestReadTime(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("timestamp") Timestamp timestamp);
    Timestamp findLatestReadTime(@Param("roomId") Long roomId, @Param("userId") Long userId);
}