package com.example.demo.util;

import com.example.demo.record.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CheatPointUpdateScheduler {
    
    private final RecordService recordService;
    
    // 매일 00시 05분에 실행
    @Scheduled(cron = "0 5 0 * * ?")
    public void updateCheatPoint() {
        System.out.println("치팅 포인트 업데이트 스케줄러 실행: " + LocalDateTime.now());
        
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        // 어제 날짜의 치팅 포인트 업데이트
        recordService.updateCheatPoint(yesterday);
        
        System.out.println("치팅 포인트 업데이트 완료, 기준 날짜: " + yesterday);
    }
}
