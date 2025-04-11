package com.example.demo.dto.record;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.sql.Date;

@Data
@Alias("ExerciseRecord")
public class ExerciseRecordDto {
    private int id;
    private int userId;
    private String exerciseType;
    private int exerciseLevel;
    private int burnedKcal;
    private int exerciseTime;
    private Date date;
}
