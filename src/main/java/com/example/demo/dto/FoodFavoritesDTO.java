package com.example.demo.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("FoodFavorites")
public class FoodFavoritesDTO {
    private int id;
    private int userId;
    private int foodId;
    private String foodType;
}
