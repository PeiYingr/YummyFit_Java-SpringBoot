package com.peiyingr.yummyfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.peiyingr.yummyfit")
public class YummyfitApplication {

	public static void main(String[] args) {
		SpringApplication.run(YummyfitApplication.class, args);
	}
}
