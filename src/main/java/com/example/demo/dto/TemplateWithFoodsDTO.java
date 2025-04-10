package com.example.demo.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Data
@Alias("TemplateWithFoods")
public class TemplateWithFoodsDTO {
    private int id;
    private String name;        // 템플릿 이름
    private int kcal;           // 칼로리
    private List<String> foodNames;  // 식품 이름 리스트만 String 컬렉션으로 저장
}
