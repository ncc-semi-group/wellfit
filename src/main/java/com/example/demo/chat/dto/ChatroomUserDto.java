package com.example.demo.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ChatroomUserDto {
    private Long userId;
    private Long roomId;
    private String nickname;
    private Timestamp latestReadTime;
    private Timestamp createdAt;
    private String profileImage;
}
