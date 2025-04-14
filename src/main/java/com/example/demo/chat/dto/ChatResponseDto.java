package com.example.demo.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ChatResponseDto {
    private Long roomId;
    private Long userId;
    private String message;
    private String createdAt;
    private String messageType;
    private String chatImage;
    private int readCount;
}
