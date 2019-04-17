package com.yash.coursera.integration.batch;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.yash.coursera.integration.model.User;

@Configuration
public class InvitationReader {

	@Bean
	public FlatFileItemReader<User> fileItemReader(@Value("${user.file}") Resource resource){
		FlatFileItemReader<User> flatFileReader = new FlatFileItemReader<>();

		flatFileReader.setResource(resource);
		flatFileReader.setName("user csv reader");
		flatFileReader.setLinesToSkip(1);
		flatFileReader.setLineMapper(lineMapper());

		return flatFileReader;		
	}


	private LineMapper<User> lineMapper(){

		DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(true);
		lineTokenizer.setNames(new String[] {"externalId", "email", "fullName", "status"});

		BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(User.class);

		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);

		return defaultLineMapper;

	}
}
