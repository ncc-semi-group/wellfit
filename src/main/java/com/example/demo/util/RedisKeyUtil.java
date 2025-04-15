package com.example.demo.util;

import java.text.MessageFormat;

public class RedisKeyUtil {
    private static final String LATEST_READ_TIME_KEY_PATTERN = "chat:latestReadTime:{0}:{1}";

    public static String getLatestReadTimeKey(Long roomId, Long userId) {
        return MessageFormat.format(LATEST_READ_TIME_KEY_PATTERN, roomId, userId);
    }
}