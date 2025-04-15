package com.example.demo.record.service;

import com.example.demo.dto.record.ExerciseRecordDto;
import com.example.demo.record.mapper.ExerciseDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExerciseDetailService {
    
    private final ExerciseDetailMapper exerciseDetailMapper;
    private final RecordService recordService;
    
    // 운동 기록 가져오기
    @Transactional
    public List<ExerciseRecordDto> getExerciseRecords(int userId, Date sqlDate) {
        ExerciseRecordDto exerciseRecord = new ExerciseRecordDto();
        exerciseRecord.setUserId(userId);
        exerciseRecord.setDate(sqlDate);
        
        // 운동 기록 가져오기
        return exerciseDetailMapper.getExerciseRecords(exerciseRecord);
    }
    
    // 운동 기록 삭제
    @Transactional
    public void deleteExerciseRecord(int exerciseId, int burnedKcal, int exerciseTime, int userId, Date sqlDate) {
        // 운동 기록 삭제
        exerciseDetailMapper.deleteExerciseRecord(exerciseId);
        
        // 통계 정보 업데이트
        ExerciseRecordDto exerciseRecord = new ExerciseRecordDto();
        exerciseRecord.setBurnedKcal(burnedKcal);
        exerciseRecord.setExerciseTime(exerciseTime);
        exerciseRecord.setUserId(userId);
        exerciseRecord.setDate(sqlDate);
        exerciseDetailMapper.updateStatisticsKcalAndTime(exerciseRecord);
        
        // 성취 여부 검증
        recordService.achievedCheck(userId, sqlDate);
    }
}
