package com.example.stageOneTask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class StageOneTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(StageOneTaskApplication.class, args);
		System.out.println("Hello");
	}

}