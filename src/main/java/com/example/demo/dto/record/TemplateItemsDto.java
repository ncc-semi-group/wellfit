package com.example.demo.dto.record;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("TemplateItems")
public class TemplateItemsDto {
    private int id;
    private int foodTemplateId;
    private int foodRecordsId;
    private int foodId;
    private String foodType;
    private int amount;
    private int serving;
}
