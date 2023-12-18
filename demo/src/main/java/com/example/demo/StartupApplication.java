package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StartupApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartupApplication.class, args);
		System.out.println("Hello Team");
		System.out.println("""
				
				Swagger-UI: http://localhost:8080/swagger-ui/index.html#/
				
				""");
	}

}
