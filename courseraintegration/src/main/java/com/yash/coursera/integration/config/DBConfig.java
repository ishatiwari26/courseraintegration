package com.yash.coursera.integration.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DBConfig {

	@Value("${com.yash.coursera.db.username}")
	String userName;

	@Value("${com.yash.coursera.db.password}")
	String password;

	@Value("${com.yash.coursera.db.url}")
	String url;

	@Value("${com.yash.coursera.db.driver}")
	String driver;

	@Bean
	@Primary
	public DataSource dataSource() {
		return DataSourceBuilder.create().username(userName).password(password).url(url).driverClassName(driver)
				.build();
	}
}
