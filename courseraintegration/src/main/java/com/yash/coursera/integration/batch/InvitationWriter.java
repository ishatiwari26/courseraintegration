package com.yash.coursera.integration.batch;

import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.yash.coursera.integration.components.CourseraTokenComponent;
import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.dao.CourseraAPIDataDao;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.ApiResponse;
import com.yash.coursera.integration.model.User;

@Component
public class InvitationWriter implements ItemWriter<User> {

	@Autowired
	BatchConfig jobConfigurer;

	@Autowired
	private CourseraAPIDataDao dao;

	@Autowired
	FileOpUtils fileOpUtils;

	@Autowired
	private CourseraTokenComponent courseraTokenComponent;

	private List<String> programIds;
	private String accessToken;
	private String refreshToken;

	@Override
	public void write(List<? extends User> users) throws Exception {

		users.forEach(user -> {
			String status = user.getStatus();
			if (status.equals(GlobalConstants.ACTIVE_STATUS)) {
				postOrDeleteInvitation(user, HttpMethod.POST);
			} else if (status.equals(GlobalConstants.TERMINATE_STATUS)) {
				postOrDeleteInvitation(user, HttpMethod.DELETE);
			} else {
				System.out.println("user details are invalid with user id : " + user.getExternalId());
			}
		});
	}

	private void postOrDeleteInvitation(User user, HttpMethod requestMethod) {

		if (!getProgramIds().isEmpty()) {
			programIds.forEach(programId -> {
				getInviteResponse(programId, user, requestMethod);
			});
		} else {
			System.out.println("programs are not registered ");
		}
	}

	private ApiResponse callInvitationAPI(String programId, User user, HttpMethod requestMethod) {
		ResponseEntity<ApiResponse> response = courseraTokenComponent.postOrDeleteInvitation(programId, accessToken, user, requestMethod);
		return response.getBody();
	}

	private ApiResponse getInviteResponse(String programId, User user, HttpMethod requestMethod) {
		ApiResponse response = null;
		try {
			if (accessToken == null) {

				Map<String, String> tokensMap = fileOpUtils.readAccessToken();
				accessToken = tokensMap.get(GlobalConstants.ACCESS_TOKEN_KEY);
				refreshToken = tokensMap.get(GlobalConstants.REFRESH_TOKEN_KEY);

			}
			if (accessToken != null) {
				response = callInvitationAPI(programId, user, requestMethod);
			}

		} catch (HttpClientErrorException responseException) {

			String apiStatus = responseException.getStatusCode().name();
			if (apiStatus.equals("UNAUTHORIZED")) {

				try {
					accessToken = jobConfigurer.getNewToken(refreshToken);
					response = callInvitationAPI(programId, user, requestMethod);
				} catch (RestClientException ex) {
					System.out.println("unauthorized user");
				}

			} else {
				System.out.println("user details are invalid with user id : " + user.getExternalId() + "  programId : "+ programId );
				return null;
			}
		}
		return response;
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		programIds = dao.getProgramIds();
	}

	public List<String> getProgramIds() {
		return programIds;
	}

	public void setProgramIds(List<String> programIds) {
		this.programIds = programIds;
	}
}
