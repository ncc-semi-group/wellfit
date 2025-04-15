package com.example.demo.chat.dto;

import com.example.demo.dto.chat.MessageType;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ChatEnterDto {
    private Long roomId;
    private Long userId;
    private Timestamp createdAt;
    private MessageType messageType = MessageType.ENTER;
}
