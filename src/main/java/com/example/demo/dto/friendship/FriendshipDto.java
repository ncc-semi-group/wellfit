package com.example.demo.dto.friendship;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("FriendshipDto")
public class FriendshipDto {
    private int id;
    private int user1Id;
    private int user2Id;
}
