package com.example.demo.record.mapper;

import com.example.demo.dto.record.FoodRecordsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecordMapper {
    
    List<FoodRecordsDto> getFoodRecords (FoodRecordsDto foodRecords);
}
