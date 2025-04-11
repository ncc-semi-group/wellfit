package com.example.demo.dto.friendship;

import lombok.Data;

@Data
public class FriendshipRequestDto {
    private int id;
    private int userId;
    private int senderId;
    private String requestMessage; 
}
