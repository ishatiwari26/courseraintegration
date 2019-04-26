package com.yash.coursera.integration.batch;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.User;

@Configuration
public class InvitationReader {

	@Value("${local.user.file.dir.path}")
	private String localPath;

	@Value("${user.file.name}")
	private String fileName;

	@Bean
	public FlatFileItemReader<User> fileItemReader() {

		FlatFileItemReader<User> flatFileReader = new FlatFileItemReader<>();
		flatFileReader.setResource(new FileSystemResource(localPath + fileName));
		flatFileReader.setName(GlobalConstants.READER_NAME);
		flatFileReader.setLinesToSkip(1);
		flatFileReader.setLineMapper(lineMapper());
		flatFileReader.setStrict(true);

		return flatFileReader;
	}

	private LineMapper<User> lineMapper() {

		DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames(new String[] {"externalId", "email", "fullName", "status"});

		BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(User.class);

		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);

		return defaultLineMapper;
	}
}
