package com.example.demo.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StringTimestampConverter {
    public static Timestamp stringToTimestamp(String datetimeStr) {
        if (datetimeStr == null || datetimeStr.isEmpty()) {
            return null;
        }
        try{
            // Z가 포함된 ISO 8601 형식 처리
            OffsetDateTime odt = OffsetDateTime.parse(datetimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return Timestamp.from(odt.toInstant());
        }catch (DateTimeParseException e){
            LocalDateTime ldt = LocalDateTime.parse(datetimeStr);  // "2025-04-08T18:23:45.123"
            return Timestamp.valueOf(ldt);
        }
    }


    public static String timestampToString(Timestamp timestamp) {
        return timestamp.toInstant()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
    }
}

