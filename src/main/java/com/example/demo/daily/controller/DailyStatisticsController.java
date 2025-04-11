package com.example.demo.daily.controller;

import com.example.demo.daily.service.DailyStatisticsService;
import com.example.demo.dto.statistics.DailyStatisticsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/daily-statistics")
@RequiredArgsConstructor
public class DailyStatisticsController {

    private final DailyStatisticsService dailyStatisticsService;

    @GetMapping("/{userId}")
    public ResponseEntity<DailyStatisticsDto> getDailyStatistics(
            @PathVariable("userId") int userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(dailyStatisticsService.getDailyStatistics(userId, date));
    }

    @GetMapping("/{userId}/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyStatistics(
            @PathVariable("userId") int userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        return ResponseEntity.ok(dailyStatisticsService.getMonthlyStatistics(userId, year, month));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDailyStatistics(
            @PathVariable("id") int id,
            @RequestBody DailyStatisticsDto statistics) {
        statistics.setId(id);
        dailyStatisticsService.updateDailyStatistics(statistics);
        return ResponseEntity.ok().build();
    }
} 