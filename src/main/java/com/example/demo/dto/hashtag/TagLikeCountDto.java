package com.example.demo.dto.hashtag;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("TagLikeCountDto")
public class TagLikeCountDto {
    private String tag;
    private int count;
} 