package com.example.demo.record.service;

import com.example.demo.dto.record.ExerciseRecordDto;
import com.example.demo.record.mapper.ExerciseDetailMapper;
import com.example.demo.record.mapper.ExerciseFormMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseFormService {
    
    private final ExerciseFormMapper exerciseFormMapper;
    private final RecordService recordService;
    private final ExerciseDetailMapper exerciseDetailMapper;
    
    @Transactional
    public void addExerciseRecord(ExerciseRecordDto exerciseRecord) {
        // 운동 기록 추가
        exerciseFormMapper.addExerciseRecord(exerciseRecord);
        
        // 통계 정보 업데이트
        List<ExerciseRecordDto> recordsList = exerciseDetailMapper.getExerciseRecords(exerciseRecord);
        int burnedKcal = 0;
        int exerciseTime = 0;
        for (ExerciseRecordDto record : recordsList) {
            burnedKcal += record.getBurnedKcal();
            exerciseTime += record.getExerciseTime();
        }
        exerciseRecord.setBurnedKcal(burnedKcal);
        exerciseRecord.setExerciseTime(exerciseTime);
        exerciseDetailMapper.updateStatisticsKcalAndTime(exerciseRecord);
        
        // 성취 여부 검증
        recordService.achievedCheck(exerciseRecord.getUserId(), exerciseRecord.getDate());
    }
    
    @Transactional
    public int getExerciseRecordCount(int userId, Date date) {
        ExerciseRecordDto exerciseRecord = new ExerciseRecordDto();
        exerciseRecord.setUserId(userId);
        exerciseRecord.setDate(date);
        return exerciseFormMapper.getExerciseRecordCount(exerciseRecord);
    }
    
    


}
