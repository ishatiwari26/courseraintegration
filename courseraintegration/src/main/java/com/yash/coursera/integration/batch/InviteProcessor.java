package com.yash.coursera.integration.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.yash.coursera.integration.components.CourseraComponent;
import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.dao.CourseraAPIDataDao;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.ApiResponse;
import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.Elements;
import com.yash.coursera.integration.model.User;

public class InviteProcessor implements ItemProcessor<User, Elements> {

	private ApiResponse apiResponse;
	private List<String> programIds;
	List<Element> apiResponseList;
	private String invitationApiUrl;

	BatchConfig jobConfigurer;
	private JobExecution jobExecution;
	private String accessToken, refreshToken;

	private CourseraAPIDataDao dao;
	private CourseraComponent courseraComponent = new CourseraComponent();

	public InviteProcessor() {
	}

	public InviteProcessor(CourseraAPIDataDao courseraAPIDataDao) {
		this.dao = courseraAPIDataDao;
	}

	@Override
	public Elements process(User user) throws Exception {

		if (!CollectionUtils.isEmpty(programIds)) {
			List<Element> tempApiResponseList = new ArrayList<>();
			programIds.forEach(programId -> {
				apiResponse = getInviteResponse(programId, user);
				if(apiResponse != null) {
					tempApiResponseList.add(apiResponse.getElements().get(0));
					apiResponseList = tempApiResponseList;
				}
			});

		} else {
			System.out.println("programs are not registered ");
			return null;
		}

		Elements elements = null;
		if(Optional.ofNullable(apiResponseList).isPresent()) {
			elements = new Elements();
			elements.setElement(apiResponseList);
		}

		return elements;
	}

	private ApiResponse callInvitationAPI(String programId, User user) {

		ResponseEntity<ApiResponse> response = courseraComponent.postInvitation(programId, accessToken, user,invitationApiUrl);
		return response.getBody();
	}

	public ApiResponse getInviteResponse(String programId, User user) {
		ApiResponse response = null;
		try {
			if (accessToken == null) {
				Map<String, String> tokensMap = FileOpUtils.readAccessToken();
				accessToken = tokensMap.get(GlobalConstants.ACCESS_TOKEN_KEY);
				refreshToken = tokensMap.get(GlobalConstants.REFRESH_TOKEN_KEY);
			}

			if (accessToken != null) {
				response = callInvitationAPI(programId, user);
			}

		} catch (HttpClientErrorException e) {

			if(e.getRawStatusCode() == 401) {
				try {
					jobConfigurer = new BatchConfig();
					accessToken = jobConfigurer.getNewToken(refreshToken);
					response = callInvitationAPI(programId, user);
				} catch (RestClientException ex) {
					// to cover condition if exception occurs in new access token generation through
					// refresh token itsel
					System.out.println("unauthorized user");
					throw ex;
				}
			}else {
				System.out.println("user is already invited with programid : " + programId);
				return null;
			}

		}
		return response;
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {

		jobExecution = stepExecution.getJobExecution();
		invitationApiUrl = jobExecution.getJobParameters().getString("apiUrl");
		programIds = dao.getProgramIds();
	}

}
