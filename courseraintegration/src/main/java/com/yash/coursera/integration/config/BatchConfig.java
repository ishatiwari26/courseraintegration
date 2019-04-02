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
import com.yash.coursera.integration.batch.InvitationReader;
import com.yash.coursera.integration.batch.InvitationWriter;
import com.yash.coursera.integration.batch.ResponseProcessor;
import com.yash.coursera.integration.batch.ResponseReader;
import com.yash.coursera.integration.batch.ResponseWriter;
import com.yash.coursera.integration.dao.CourseraAPIDataDao;
import com.yash.coursera.integration.helper.BatchConfigComponent;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.Elements;
import com.yash.coursera.integration.model.SFLmsMapper;

@Component
public class BatchConfig {

	@Autowired
	StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	CourseraAPIDataDao dao;

	@Value("${GET_LOCAL_CONTENT_URL}")
	private String localContentApiUrl;

	@Value("${limit.per.batch.read.operation}")
	private Integer limitCountPerRead;

	@Value("${GET_PROGRAM_API}")
	private String getProgramListApi;

	@Value("${GET_CONTENTS_API}")
	private String getContentsApi;

	@Value("${REFRESH_TOKEN}")
	private String refreshTokenParamValue;

	@Value("${ACCESS_TYPE}")
	private String accessTypeParamValue;

	@Value("${CLIENT_SECRET}")
	private String clientSecret;

	@Value("${CLIENT_ID}")
	private String clientId;

	@Value("${CALLBACK_URI}")
	private String callBackUri;

	@Value("${AUTHORIZATION_CODE}")
	private String authCodeParamValue;

	@Value("${AUTH_TOKEN_URI}")
	private String getAuthTokenUri;

	@Value("${GET_CODE_URI}")
	private String getCodeUri;

	@Value("${GET_LOCAL_INVITATION_URL}")
	private String localInvitationApiUrl;

	@Autowired
	private JobBuilderFactory jobs;
	
	@Autowired
	BatchConfigComponent batchConfigComponent;

	public Job processJob() {
		return jobs.get("processJob").incrementer(new RunIdIncrementer()).flow(getStep()).end().build();
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
		ResponseReader reader = new ResponseReader(GlobalConstants.REQUEST_METHOD, 0, this, limitCountPerRead);
		return reader;
	}

	public ItemProcessor<Elements, List<SFLmsMapper>> processor() {
		return new ResponseProcessor();
	}

	public ItemWriter<List<SFLmsMapper>> writer() {
		ResponseWriter writer = new ResponseWriter();
		return writer;
	}
	
	public ItemWriter<List<SFLmsMapper>> dbwriter() {
		CustomDBWriter writer = new CustomDBWriter(dao);
		return writer;
	}

	public Job processInviteJob() {
		return jobs.get("invitation").incrementer(new RunIdIncrementer()).flow(sendInviteStep()).end().build();
	}

	public Step sendInviteStep() {

		Step stepInviteApiCall = null;
		try {
			stepInviteApiCall = stepBuilderFactory.get(GlobalConstants.STEP_NAME).allowStartIfComplete(false)
					.<Elements, Elements>chunk(1).reader(inviteReader()).writer(inviteWriter()).build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return stepInviteApiCall;
	}

	public ItemReader<Elements> inviteReader() throws MalformedURLException {
		InvitationReader reader = new InvitationReader(localInvitationApiUrl, "POST");
		return reader;
	}

	public ItemWriter<Elements> inviteWriter() {
		InvitationWriter writer = new InvitationWriter();
		return writer;
	}

	public String getNewToken(String refreshToken) {
		return batchConfigComponent.callGetNewAccessToken(refreshToken);
	}

}
