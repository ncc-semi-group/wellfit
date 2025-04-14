package com.example.demo.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatroomDto {
    private Long roomId;
    private String roomName;
    private String roomImage;
    private String description;
    private String readingCount;
    private String createdAt;
    private int maxUser;
    private int userCount;
    private String latestMessage;
    @Override
    public String toString() {
        return "ChatroomDto{" +
                "roomId=" + roomId +
                ", roomName='" + roomName + '\'' +
                ", roomImage='" + roomImage + '\'' +
                ", description='" + description + '\'' +
                ", readingCount='" + readingCount + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", maxUser=" + maxUser +
                ", userCount=" + userCount +
                '}';
    }
}
