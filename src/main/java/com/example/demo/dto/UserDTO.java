package com.example.demo.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Data
@Alias("User")
public class UserDTO {
    private int id;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;
    private String gender;
    private int age;
    private int height;
    private String activityLevel;
    private int currentWeight;
    private int goalWeight;
    private String type;
    private int carbohydrate;
    private int protein;
    private int fat;
    private Timestamp createAt;
}
