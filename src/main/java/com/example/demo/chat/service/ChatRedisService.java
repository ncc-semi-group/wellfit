package com.example.demo.chat.service;

import com.example.demo.chat.mapper.ChatroomMapper;
import com.example.demo.chat.mapper.ChatroomUserMapper;
import com.example.demo.util.RedisKeyUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ChatRedisService {

    private final StringRedisTemplate redisTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ChatroomMapper chatroomMapper;
    private final ChatroomUserMapper chatroomUserMapper;

    // 생성자 주입
    public ChatRedisService(StringRedisTemplate redisTemplate, ChatroomMapper chatroomMapper, ChatroomUserMapper chatroomUserMapper) {
        this.redisTemplate = redisTemplate;
        this.chatroomMapper = chatroomMapper;
        this.chatroomUserMapper = chatroomUserMapper;
    }

    // 최신 읽기 시간 업데이트
    public Timestamp updateLatestReadTime(Long roomId, Long userId, Timestamp timestamp) {
        String key = RedisKeyUtil.getLatestReadTimeKey(roomId, userId);
        try {
            // 타임스탬프에서 long 값(Unix 타임스탬프)을 추출하여 Redis에 저장
            long unixTimestamp = timestamp.getTime(); // 밀리초 단위로 타임스탬프 저장
            redisTemplate.opsForValue().set(key, String.valueOf(unixTimestamp));
        } catch (Exception e) {
            // 예외 처리 및 로깅
            System.err.println("Error updating latest read time for roomId: " + roomId + " and userId: " + userId);
            e.printStackTrace();
        }
        return timestamp; // 업데이트된 타임스탬프 반환
    }

    // 최신 읽기 시간 가져오기
    public Timestamp getLatestReadTime(Long roomId, Long userId) {
        String key = RedisKeyUtil.getLatestReadTimeKey(roomId, userId);
        String timestampStr = redisTemplate.opsForValue().get(key);

        if (timestampStr != null) {
            try {
                // 문자열을 long 타입의 Unix 타임스탬프로 변환 후 Timestamp로 변환
                long timestamp = Long.parseLong(timestampStr);
                return new Timestamp(timestamp);
            } catch (NumberFormatException e) {
                // 잘못된 형식일 경우 null 반환
                System.err.println("Invalid timestamp format: " + timestampStr);
                return null;
            }
        } else {
            return null; // 없으면 null 반환
        }
    }

    // 최신 읽기 시간 삭제
    public void deleteLatestReadTime(Long roomId, Long userId) {
        try {
            redisTemplate.delete(RedisKeyUtil.getLatestReadTimeKey(roomId, userId));
        } catch (Exception e) {
            // 예외 처리 및 로깅
            System.err.println("Error deleting latest read time for roomId: " + roomId + " and userId: " + userId);
            e.printStackTrace();
        }
    }

    // Redis에 있는 모든 최신 읽기 시간 데이터를 DB로 옮기기
    public void migrateAllLatestReadTimesToDb() {
        // 모든 채팅방의 최신 읽기 시간을 가져와서 DB에 저장하는 로직
        List<Long> allRoomIds = chatroomMapper.findAllRoomIds(); // 채팅방 목록을 가져오는 방법은 각 프로젝트에 맞게 처리
        for (Long roomId : allRoomIds) {
            List<Long> allUserIds = chatroomUserMapper.findChatroomUserByChatroomId(roomId).stream().map(r->r.getUserId()).toList(); // 각 채팅방에 속한 유저 목록을 가져오는 방법은 각 프로젝트에 맞게 처리
            for (Long userId : allUserIds) {
                String key = RedisKeyUtil.getLatestReadTimeKey(roomId, userId);
                String timestampStr = redisTemplate.opsForValue().get(key);

                if (timestampStr != null) {
                    try {
                        long timestamp = Long.parseLong(timestampStr);
                        Timestamp timestampObj = new Timestamp(timestamp);

                        // DB에 저장
                        chatroomUserMapper.updateLatestReadTime(roomId, userId, timestampObj); // DB에 저장하는 메서드 호출
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid timestamp format: " + timestampStr);
                    }
                }
            }
        }
    }
}
