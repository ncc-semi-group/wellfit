package com.example.demo.record.service;

import com.example.demo.dto.FoodRecordsDTO;
import com.example.demo.record.mapper.RecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordService {
    
    private final RecordMapper recordMapper;
    
    @Transactional
    public List<FoodRecordsDTO> getFoodRecords (int userId, Date date) {
        FoodRecordsDTO foodRecords = new FoodRecordsDTO();
        foodRecords.setUserId(userId);
        foodRecords.setDate(date);
        return recordMapper.getFoodRecords(foodRecords);
    }
    
}
