package com.yash.coursera.integration.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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

@Component
public class InviteProcessor implements ItemProcessor<User, Elements> {
	
	@Autowired
	BatchConfig jobConfigurer;
	
	@Autowired
	private CourseraAPIDataDao dao;
	
	@Autowired
	private CourseraComponent courseraComponent;

	private ApiResponse apiResponse;
	private List<String> programIds;
	List<Element> apiResponseList;
	private String accessToken;
	private String refreshToken;

	@Override
	public Elements process(User user) throws Exception {

		if (!CollectionUtils.isEmpty(programIds)) {
			apiResponseList = null;
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

		ResponseEntity<ApiResponse> response = courseraComponent.postInvitation(programId, accessToken, user);
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
		programIds = dao.getProgramIds();
	}

}
