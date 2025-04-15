package com.example.demo.dto.chat;

import com.example.demo.chat.dto.ChatResponseDto;
import com.example.demo.util.StringTimestampConverter;
import lombok.*;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;
@Alias("Chat")
@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    private Long chatId;
    private Long chatroomId;
    private Long userId;
    private MessageType messageType;
    private String message;
    private Timestamp createdAt;
    private int readCount;
    private String imageUrl;

    public ChatResponseDto toResponseDto() {
        return ChatResponseDto.builder()
                .roomId(chatroomId)
                .userId(userId)
                .message(message)
                .readCount(readCount)
                .createdAt(StringTimestampConverter.timestampToString(createdAt))
                .messageType(messageType.toString())
                .build();
    }
    @Override
    public String toString() {
        return "Chat{" +
                "chatId=" + chatId +
                ", chatroomId=" + chatroomId +
                ", userId=" + userId +
                ", messageType=" + messageType +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                ", readCount=" + readCount +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
