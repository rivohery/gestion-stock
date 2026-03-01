package com.alibou.stockmanage;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class StockManagmentApplication {
	public static void main(String[] args) {
		SpringApplication.run(StockManagmentApplication.class, args);
	}



}
