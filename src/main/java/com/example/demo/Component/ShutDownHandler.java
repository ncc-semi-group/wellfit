package com.example.demo.Component;

import com.example.demo.chat.mapper.ChatroomMapper;
import com.example.demo.chat.service.ChatRedisService;
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
    private final ChatRedisService chatRedisService;
    @PreDestroy
    public void onShutDown(){
        log.info("🛑 서버 종료 - 모든 채팅방 유저 제거");
        chatroomMapper.clearAllActiveUsers();
        log.info("🛑 서버 종료 - Redis에 저장된 모든 최신 읽기 시간 DB로 마이그레이션");
        chatRedisService.migrateAllLatestReadTimesToDb();
    }
}
