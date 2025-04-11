package com.example.demo.dto.chat;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;
@Alias("ChatroomUser")
@Data
@Setter
@Getter
public class ChatroomUser {
    private Long chatroomUserId;
    private Long chatroomId;
    private Long userId;
    private String nickname;
    private String chatroomName;
    private Timestamp create_at;
    private Timestamp latest_read_time;
}
