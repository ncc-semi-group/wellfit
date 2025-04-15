package com.example.demo.chat.dto;

import com.example.demo.dto.chat.Chatroom;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatroomCreateDto {
    private Long userId;
    private String roomName;
    private int maxUser = 10;
    private String description;
    private String imageUrl;
    public ChatroomCreateDto(Long userId, String roomName,int maxUser, String description, String imageUrl) {
        this.userId = userId;
        this.roomName = roomName;
        this.maxUser = maxUser;
        this.description = description;
        this.imageUrl = imageUrl;
    }
    public Chatroom toChatroom() {
        return Chatroom.builder()
                .chatroomName(roomName)
                .maxUser(maxUser)
                .description(description)
                .roomImage(imageUrl)
                .build();
    }
}
