package com.example.demo.Component;

import com.example.demo.chat.mapper.ChatroomUserMapper;
import com.example.demo.chat.service.ChatRedisService;
import com.example.demo.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatScheduler {

    private final ChatRedisService chatRedisService;
    private final ChatService chatService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ChatroomUserMapper chatroomUserMapper;

    @Scheduled(fixedRate = 5 * 60 * 1000)  // 5분마다
    public void flushReadTimesToDB() {
        Set<String> keys = scanKeys("chat:readtime:*");

        for (String key : keys) {
            String[] parts = key.split(":");  // "chat:readtime:4:9" → ["chat", "readtime", "4", "9"]
            if (parts.length != 4) continue;

            Long roomId = Long.parseLong(parts[2]);
            Long userId = Long.parseLong(parts[3]);

            // DB 업데이트 호출 (예: MyBatis Mapper)
            try {
                chatroomUserMapper.updateLatestReadTime(roomId, userId);
                log.info("✅ Flushed latestReadTime to DB - roomId={}, userId={}", roomId, userId);
            } catch (Exception e) {
                log.error("❌ Failed to flush readTime for roomId={}, userId={}", roomId, userId, e);
            }

            // Redis 키 삭제(Optional) → or 만료 설정만 유지
            redisTemplate.delete(key);
        }
    }

    private Set<String> scanKeys(String pattern) {
        Set<String> keys = new HashSet<>();

        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();
        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options)) {
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keys;
    }

}