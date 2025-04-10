package com.example.demo.record.mapper;

import com.example.demo.dto.FoodRecordsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecordMapper {
    
    List<FoodRecordsDTO> getFoodRecords (FoodRecordsDTO foodRecords);
}
