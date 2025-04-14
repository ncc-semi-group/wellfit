package com.example.demo.dto.record;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("RecordItems")
public class RecordItemsDto {
    private int id;
    private int foodRecordsId;
    private int foodId;
    private String foodType;
    private int amount;
    private int serving;
}
