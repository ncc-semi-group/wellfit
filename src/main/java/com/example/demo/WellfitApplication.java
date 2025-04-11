package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"data.*", "*.controller", "com.example.demo"})
@MapperScan({"com.example.demo.badge.mapper", "com.example.demo.board.mapper", "com.example.demo.user.mapper", "com.example.demo.comment.mapper", "com.example.demo.daily.mapper", "com.example.demo.friendship.mapper", ""})
public class WellfitApplication {

	public static void main(String[] args) {
		SpringApplication.run(WellfitApplication.class, args);
	}

}
