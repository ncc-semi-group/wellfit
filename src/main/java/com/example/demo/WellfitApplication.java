package com.example.demo;

import com.example.demo.ncp.storage.NaverConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(NaverConfig.class)
public class WellfitApplication {
	public static void main(String[] args) {
		SpringApplication.run(WellfitApplication.class, args);
	}

}
