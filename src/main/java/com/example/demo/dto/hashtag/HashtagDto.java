package com.example.demo.dto.hashtag;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("HashtagDto")
public class HashtagDto {
    private int id;
    private String tag;
} 