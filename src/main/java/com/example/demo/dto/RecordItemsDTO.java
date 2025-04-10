package com.example.demo.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("RecordItems")
public class RecordItemsDTO {
    private int id;
    private int foodRecordsId;
    private int foodId;
    private String foodType;
    private int amount;
    private int serving;
}
