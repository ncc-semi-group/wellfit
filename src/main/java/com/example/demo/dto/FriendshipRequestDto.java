package com.example.demo.dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("FriendshipRequestDto")
public class FriendshipRequestDto {
    private int id;
    private int userId;
    private int senderId;
    private String requestMessage; 
}
