package com.example.demo.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("FoodNutrition")
public class FoodNutritionDto {
    private int id;
    private int userId;
    private int foodId;
    private String foodType;
    private String name;
    private String standard;
    private int kcal;
    private float protein;
    private float fat;
    private float carbohydrate;
    private float sugar;
    private int natrium;
    private float cholesterol;
    private float saturatedFat;
    private float transFat;
    private String manufacturerName;
    private int servingSize;
    private float weight;
    private float amount;
    private int serving;
}
