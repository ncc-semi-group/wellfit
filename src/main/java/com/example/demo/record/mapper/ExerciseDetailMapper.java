package com.example.demo.record.mapper;

import com.example.demo.dto.record.ExerciseRecordDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExerciseDetailMapper {
    
    List<ExerciseRecordDto> getExerciseRecords(ExerciseRecordDto exerciseRecord);
    
    void deleteExerciseRecord(int exerciseId);
    
    void updateStatisticsKcalAndTime(ExerciseRecordDto exerciseRecord);
}
