package com.example.demo.dto.chat;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;
@Alias("Chatroom")
@Data
@Setter
@Getter
@Builder
public class Chatroom {
    private Long id;
    private String chatroomName;
    private Timestamp createdAt;
    private int maxUser;
    private String roomImage;
    private String description;
}
