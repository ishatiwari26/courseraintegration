package com.yash.coursera.integration.config;

import java.net.MalformedURLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.yash.coursera.integration.batch.CustomDBWriter;
import com.yash.coursera.integration.batch.ResponseProcessor;
import com.yash.coursera.integration.batch.ResponseReader;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.Elements;
import com.yash.coursera.integration.model.SFLmsMapper;

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
	
	@Bean
	public ItemReader<Elements> reader() throws MalformedURLException {
		ResponseReader reader = new ResponseReader();
		return reader;
	}

	@Bean
	public ItemProcessor<Elements, List<SFLmsMapper>> processor() {
		return new ResponseProcessor();
	}

	@Bean
	public ItemWriter<List<SFLmsMapper>> dbwriter() {
		CustomDBWriter writer = new CustomDBWriter();
		return writer;
	}
}
