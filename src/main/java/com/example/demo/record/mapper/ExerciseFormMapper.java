package com.example.demo.record.mapper;

import com.example.demo.dto.record.ExerciseRecordDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExerciseFormMapper {
    
    void addExerciseRecord(ExerciseRecordDto exerciseRecord);
    
    int getExerciseRecordCount(ExerciseRecordDto exerciseRecord);
}
