package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"data.*"})
@MapperScan("data.mapper")
public class WellfitApplication {

	public static void main(String[] args) {
		SpringApplication.run(WellfitApplication.class, args);
	}

}
