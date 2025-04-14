package com.example.demo.record.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "food_final")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodDocument {
    
    @Id
    private Long id;
    
    @Field(type = FieldType.Text)
    private String name;
    
    @Field(type = FieldType.Text, name = "manufacturer_name")
    private String manufacturerName;
    
    @Field(type = FieldType.Integer)
    private Integer kcal;
    
    @Field(type = FieldType.Float, name = "carbohydrate")
    private Float carbohydrate;
    
    @Field(type = FieldType.Float, name = "protein")
    private Float protein;
    
    @Field(type = FieldType.Float, name = "fat")
    private Float fat;
    
    @Field(type = FieldType.Float, name = "sugar")
    private Float sugar;
    
    @Field(type = FieldType.Float, name = "saturated_fat")
    private Float saturatedFat;
    
    @Field(type = FieldType.Float, name = "trans_fat")
    private Float transFat;
    
    @Field(type = FieldType.Float, name = "cholesterol")
    private Float cholesterol;
    
    @Field(type = FieldType.Integer, name = "natrium")
    private Integer natrium;
    
    @Field(type = FieldType.Text, name = "standard")
    private String standard;
    
    @Field(type = FieldType.Float, name = "weight")
    private Float weight;
    
    @Field(type = FieldType.Integer, name = "serving_size")
    private Integer servingSize;
    
}