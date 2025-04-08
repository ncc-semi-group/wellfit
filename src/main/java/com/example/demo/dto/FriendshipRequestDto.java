package com.example.demo.dto;

import lombok.Data;

@Data
public class FriendshipRequestDto {
    private int id;
    private int userId;
    private int senderId;
    private String requestMessage; 
}
