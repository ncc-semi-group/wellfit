package com.example.demo.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class LatestReadTimeDto {
    private Long roomId;
    private Long userId;
    private Timestamp latestReadTime;
}
