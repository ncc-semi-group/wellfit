package com.example.demo.dto.board;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("FollowDto")
public class FollowDto {
		private int id;
	    private int currentUserId; // 현재 유저의 ID
	    private int targetUserId;  // 팔로우할 상대 유저의 ID

}
