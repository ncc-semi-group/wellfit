package com.example.demo.chat.dto;

import com.example.demo.dto.chat.Chat;
import com.example.demo.dto.chat.MessageType;
import com.example.demo.util.StringTimestampConverter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class ChatRequestDto {
    private Long userId;
    private Long roomId;
    private String message;
    private Timestamp createdAt;
    private String messageType;
    private String imageUrl;
    public Chat toEntity(MessageType type){
        Chat chat = Chat.builder()
                .userId(userId)
                .chatroomId(roomId)
                .messageType(type)
                .imageUrl(imageUrl)
                .build();
        if(type == MessageType.TALK){
            chat.setMessage(message);
        } else {
            chat.setMessage(type.message);
        }
        if(createdAt != null){
            chat.setCreatedAt(createdAt);
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
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
