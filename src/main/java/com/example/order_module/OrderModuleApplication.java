package com.example.order_module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OrderModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderModuleApplication.class, args);
	}

}
