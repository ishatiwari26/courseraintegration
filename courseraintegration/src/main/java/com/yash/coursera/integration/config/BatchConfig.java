package com.yash.coursera.integration.config;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.yash.coursera.integration.batch.CustomDBWriter;
import com.yash.coursera.integration.batch.InvitationWriter;
import com.yash.coursera.integration.batch.ResponseProcessor;
import com.yash.coursera.integration.batch.ResponseReader;
import com.yash.coursera.integration.components.CourseraTokenComponent;
import com.yash.coursera.integration.dao.CourseraAPIDataDao;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.Elements;
import com.yash.coursera.integration.model.SFLmsMapper;
import com.yash.coursera.integration.model.User;

@Component
public class BatchConfig {
	@Value("${limit.per.batch.read.operation}")
	private Integer limitCountPerRead;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	CourseraTokenComponent courseraTokenComponent;
	
	@Autowired
	ResponseReader reader;
	
	@Autowired
	ResponseProcessor processor;
	
	@Autowired
	CustomDBWriter writer;

	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Autowired
	CourseraAPIDataDao courseraAPIDataDao;

	@Autowired
	ItemReader<User> itemReader;

	@Autowired
	InvitationWriter inviteWriter;

	public Job processJob() {
		return jobBuilderFactory.get("processJob").incrementer(new RunIdIncrementer()).flow(getStep()).end().build();
	}

	public Step getStep() {

		Step stepContentApiCall = null;
		try {
			stepContentApiCall = stepBuilderFactory.get(GlobalConstants.STEP_NAME).allowStartIfComplete(false)
					.<Elements, List<SFLmsMapper>>chunk(1).reader(reader()).processor(processor()).writer(dbwriter())
					.build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return stepContentApiCall;
	}

	public ItemReader<Elements> reader() throws MalformedURLException {
	//	ResponseReader reader = new ResponseReader(this, limitCountPerRead);
		reader.setJobConfigurer(this);
		reader.setLimitCountPerRead(limitCountPerRead);
		reader.setIndex(0);
		return reader;
	}

	public ItemProcessor<Elements, List<SFLmsMapper>> processor() {
		return processor;
	}

	public ItemWriter<List<SFLmsMapper>> dbwriter() {
	//	CustomDBWriter writer = new CustomDBWriter(courseraAPIDataDao);
		writer.setDao(courseraAPIDataDao);
		return writer;
	}

	public Job processInviteJob() {
		return jobBuilderFactory.get("invitationJob").incrementer(new RunIdIncrementer()).flow(inviteStep()).end().build();
	}

	public Step inviteStep() {

		Step stepInviteApiCall = null;
		stepInviteApiCall = stepBuilderFactory.get(GlobalConstants.STEP_NAME).allowStartIfComplete(false)
				.<User, User>chunk(3)
				.reader(itemReader)
				.writer(inviteWriter)
				.build();

		return stepInviteApiCall;
	}
	
	public String getNewToken(String refreshToken) {
		return courseraTokenComponent.getNewAccessToken(refreshToken);
	}

}
