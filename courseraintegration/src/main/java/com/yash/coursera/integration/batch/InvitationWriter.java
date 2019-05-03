package com.yash.coursera.integration.batch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.yash.coursera.integration.components.CourseraTokenComponent;
import com.yash.coursera.integration.components.EmailTLSComponent;
import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.dao.CourseraAPIDataDao;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.ApiResponse;
import com.yash.coursera.integration.model.EmailContents;
import com.yash.coursera.integration.model.InvitationParameters;
import com.yash.coursera.integration.model.User;

@Component
public class InvitationWriter implements ItemWriter<User> {

	@Autowired
	BatchConfig jobConfigurer;

	@Autowired
	private CourseraAPIDataDao dao;

	@Autowired
	private FileOpUtils fileOpUtils;

	@Autowired
	private CourseraTokenComponent courseraTokenComponent;

	@Autowired
	private EmailTLSComponent emailComponent;

	private InvitationParameters invitationParameters;
	private String accessToken;
	private String refreshToken;

	@Override
	public void write(List<? extends User> users) {

		users.forEach(user -> {
			String status = user.getStatus();
			String userName = user.getFullName().equals("") ? null : user.getFullName();
			user.setFullName(userName);

			if (status.equalsIgnoreCase(GlobalConstants.ACTIVE_STATUS) || status.equals("")) {
				postOrDeleteInvitation(user, HttpMethod.POST);
			} else if (status.equalsIgnoreCase(GlobalConstants.TERMINATE_STATUS)) {
				postOrDeleteInvitation(user, HttpMethod.DELETE);
			} else {
				invitationParameters.getUnsuccessfulUserInvite().put(user.getExternalId(), GlobalConstants.ERROR_STATUS_MESSAGE);
			}
		});
	}

	private void postOrDeleteInvitation(User user, HttpMethod requestMethod) {
		invitationParameters.setFlag(true);
		if (!invitationParameters.getProgramIds().isEmpty()) {
			invitationParameters.getProgramIds().forEach(programId ->
			getInviteResponse(programId, user, requestMethod)
					);
		}
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
				addSuccessfulInviteToList(user);
			}

		} catch (HttpClientErrorException responseException) {
			String apiStatus = responseException.getStatusCode().name();
			if (!apiStatus.equals("BAD_REQUEST")) {
				try {
					accessToken = jobConfigurer.getNewToken(refreshToken);
					response = callInvitationAPI(programId, user, requestMethod);

					addSuccessfulInviteToList(user);

				} catch (HttpClientErrorException exception) {
					handleInvalidUserData(exception, user);
				}

			} else {
				handleInvalidUserData(responseException, user);
			}
		}

		return response;
	}

	private ApiResponse callInvitationAPI(String programId, User user, HttpMethod requestMethod) {
		ResponseEntity<ApiResponse> response = courseraTokenComponent.postOrDeleteInvitation(programId, accessToken, user, requestMethod);
		return response.getBody();
	}

	private void handleInvalidUserData(HttpClientErrorException responseException, User user) {
		String errorMessage = responseException.getResponseBodyAsString();
		Boolean inviteStatus = !(errorMessage.contains(GlobalConstants.ERROR_ID_MESSAGE) || 
				errorMessage.contains(GlobalConstants.ERROR_EMAIL_MESSAGE)) ? addUnsuccessfulInviteToMap(user, responseException) : addSuccessfulInviteToList(user);
	}

	private Boolean addSuccessfulInviteToList(User user) {
		if(invitationParameters.getFlag()) {
			System.out.println("successful invitation");
			invitationParameters.getSuccessfulUserInvite().add(user.getExternalId());
			invitationParameters.setFlag(false);

			return true;
		}

		return false;
	}

	private Boolean addUnsuccessfulInviteToMap(User user, HttpClientErrorException responseException) {
		if(invitationParameters.getFlag()) {
			System.out.println("unsuccessful invitation");
			invitationParameters.getUnsuccessfulUserInvite().put(user.getExternalId(), responseException.getMessage());
			invitationParameters.setFlag(false);
			return true;
		}

		return false;
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		invitationParameters = new InvitationParameters();
		invitationParameters.setProgramIds(dao.getProgramIds());
		invitationParameters.setSuccessfulUserInvite(new ArrayList<>());
		invitationParameters.setUnsuccessfulUserInvite(new HashMap<>());
	}

	@AfterStep
	public void afterStep(StepExecution stepExecution) {

		Integer totalSuccessfulInvite =  invitationParameters.getSuccessfulUserInvite().size();
		Integer totalUnsuccessfulInvite = invitationParameters.getUnsuccessfulUserInvite().size();

		EmailContents mailContents = new EmailContents();
		mailContents.setMailSubject("Coursera User SFTP");
		mailContents.setCurrentDate(LocalDate.now());
		mailContents.setSuccessInvitationCount(totalSuccessfulInvite);
		mailContents.setFailedInvitationCount(totalUnsuccessfulInvite);
		mailContents.setTotalInvitationCount(totalSuccessfulInvite + totalUnsuccessfulInvite);

		emailComponent.sendEmailForInviteAPIFailure(mailContents, invitationParameters.getUnsuccessfulUserInvite());
	}

	public InvitationParameters getInvitationStatus() {
		return invitationParameters;
	}

	public void setInvitationStatus(InvitationParameters invitationStatus) {
		this.invitationParameters = invitationStatus;
	}

}
