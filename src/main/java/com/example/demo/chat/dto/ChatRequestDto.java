package com.example.demo.chat.dto;

import com.example.demo.dto.chat.Chat;
import com.example.demo.dto.chat.MessageType;
import com.example.demo.util.StringTimestampConverter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatRequestDto {
    private String userId;
    private String roomId;
    private String message;
    private String createdAt;
    private String messageType;
    public Chat toEntity(MessageType type){
        Chat chat = Chat.builder()
                .userId(Long.parseLong(userId))
                .chatroomId(Long.parseLong(roomId))
                .messageType(type)
                .build();
        if(type == MessageType.TALK){
            chat.setMessage(message);
        } else {
            chat.setMessage(type.message);
        }
        if(createdAt != null){
            chat.setCreatedAt(StringTimestampConverter.stringToTimestamp(createdAt));
        }
        return chat;
    }
    @Override
    public String toString() {
        return "ChatRequestDto{" +
                "userId='" + userId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                ", messageType='" + messageType + '\'' +
                '}';
    }
}
