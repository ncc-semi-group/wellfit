package com.example.demo.Component;

import com.example.demo.chat.mapper.ChatroomMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ShutDownHandler {
    private final ChatroomMapper chatroomMapper;

    @PreDestroy
    public void onShutDown(){
        log.info("🛑 서버 종료 - 모든 채팅방 유저 제거");
        chatroomMapper.clearAllActiveUsers();
    }
}
