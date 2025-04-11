package com.example.demo.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("HashtagDto")
public class HashtagDto {
    private int id;
    private String tag;
} 