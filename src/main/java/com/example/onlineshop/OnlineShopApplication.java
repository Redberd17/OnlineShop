package com.example.onlineshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineShopApplication {
	private final Logger logger = LoggerFactory.getLogger(OnlineShopApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OnlineShopApplication.class, args);
	}
}
