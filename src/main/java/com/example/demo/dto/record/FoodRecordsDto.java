package com.example.demo.dto.record;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.sql.Date;

@Data
@Alias("FoodRecords")
public class FoodRecordsDto {
    private int id;
    private int userId;
    private String mealType;
    private Date date;
    private int kcal;
}
