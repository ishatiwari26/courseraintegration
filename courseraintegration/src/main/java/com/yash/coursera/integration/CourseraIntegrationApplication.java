package com.yash.coursera.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CourseraIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseraIntegrationApplication.class, args);
		
	}

}
