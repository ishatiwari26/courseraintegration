package com.yash.coursera.integration.batch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.StepExecution;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import com.yash.coursera.integration.components.CourseraTokenComponent;
import com.yash.coursera.integration.components.TLSEmailComponent;
import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.dao.CourseraAPIDataDao;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.ApiResponse;
import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.EmailContents;
import com.yash.coursera.integration.model.InvitationParameters;
import com.yash.coursera.integration.model.User;

@RunWith(SpringRunner.class)
public class InvitationWriterTest {

	@InjectMocks
	private InvitationWriter invitationWriter;

	@Mock
	private BatchConfig jobConfigurer;

	@Mock
	private CourseraAPIDataDao dao;

	@Mock
	private CourseraTokenComponent courseraTokenComponent;

	@Mock
	private StepExecution stepExecution;

	@Mock
	private FileOpUtils fileOpUtils;

	@Mock
	private TLSEmailComponent emailComponent;

	@Test
	public void shouldExecuteBeforeSetup() {
		when(dao.getProgramIds()).thenReturn(getProgramIdList());

		invitationWriter.beforeStep(stepExecution);
		verify(dao).getProgramIds();
	}

	@Test
	public void shouldSendInviteIfUserStatusIsActive() throws Exception {
		invitationWriter.setInvitationStatus(getInvitationStatusParameters());
		ResponseEntity<ApiResponse> response = new ResponseEntity<ApiResponse>(getApiResponse(), HttpStatus.CREATED);

		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		when(courseraTokenComponent.postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)
				, Mockito.any(HttpMethod.class))).thenReturn(response);

		invitationWriter.write(getActiveUsersList());

		verify(fileOpUtils).readAccessToken();
		verify(courseraTokenComponent, times(4)).postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)
				, Mockito.any(HttpMethod.class));
	}

	@Test
	public void shouldDeleteInviteIfUserStatusIsTerminated() throws Exception {
		invitationWriter.setInvitationStatus(getInvitationStatusParameters());
		ResponseEntity<ApiResponse> response = new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		when(courseraTokenComponent.postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)
				, Mockito.any(HttpMethod.class))).thenReturn(response);

		invitationWriter.write(getTerminateUsersList());

		verify(fileOpUtils).readAccessToken();
		verify(courseraTokenComponent, times(4)).postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)
				, Mockito.any(HttpMethod.class));

	}

	@Test
	public void shouldNotInviteOrDeleteInvitationIfStatusIsInvalid() throws Exception {
		invitationWriter.setInvitationStatus(getInvitationStatusParameters());
		ResponseEntity<ApiResponse> response = new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		when(courseraTokenComponent.postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)
				, Mockito.any(HttpMethod.class))).thenReturn(response);

		invitationWriter.write(getInvalidUsersList());

		assertEquals(2, invitationWriter.getInvitationStatus().getUnsuccessfulUserInvite().size());

		verify(fileOpUtils, times(0)).readAccessToken();
		verify(courseraTokenComponent, times(0)).postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)
				, Mockito.any(HttpMethod.class));

	}

	@Test
	public void shouldNotInviteOrDeleteInvitationIfProgramIdsIsEmpty() throws Exception {
		InvitationParameters invitationStatus = new InvitationParameters();
		invitationStatus.setProgramIds(new ArrayList<>());

		invitationWriter.setInvitationStatus(invitationStatus);

		ResponseEntity<ApiResponse> response = new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		when(courseraTokenComponent.postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)
				, Mockito.any(HttpMethod.class))).thenReturn(response);

		invitationWriter.write(getActiveUsersList());

		verify(fileOpUtils, times(0)).readAccessToken();
		verify(courseraTokenComponent, times(0)).postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)
				, Mockito.any(HttpMethod.class));
	}

	@Test
	public void shouldReturnResponseIfNewTokenGeneratedUsingRefreshToken()  throws Exception {
		String accessToken = "newAccessToken";
		invitationWriter.setInvitationStatus(getInvitationStatusParameters());

		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());

		ResponseEntity<ApiResponse> response = new ResponseEntity<ApiResponse>(getApiResponse(), HttpStatus.CREATED);
		when(courseraTokenComponent.postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class), Mockito.any(HttpMethod.class)))
		.thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED))
		.thenReturn(response);

		when(jobConfigurer.getNewToken(Mockito.any(String.class))).thenReturn(accessToken);
		invitationWriter.write(getActiveUsersList());

		assertEquals(2, invitationWriter.getInvitationStatus().getSuccessfulUserInvite().size());
		verify(fileOpUtils).readAccessToken();
		verify(courseraTokenComponent, times(5)).postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)
				, Mockito.any(HttpMethod.class));
	}


	@Test
	public void shouldHandleExceptionAfterNewTokenGneratedUsingRefreshToken() {
		String accessToken = "newAccessToken";

		invitationWriter.setInvitationStatus(getInvitationStatusParameters());
		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());

		when(courseraTokenComponent.postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class), Mockito.any(HttpMethod.class)))
		.thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED))
		.thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "INVITATION_ALREADY_EXISTS".getBytes(), null));

		when(jobConfigurer.getNewToken(Mockito.any(String.class))).thenReturn(accessToken);

		invitationWriter.write(getActiveUsersList());

		assertEquals(2, invitationWriter.getInvitationStatus().getSuccessfulUserInvite().size());
		verify(fileOpUtils).readAccessToken();
		verify(courseraTokenComponent, times(5)).postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)
				, Mockito.any(HttpMethod.class));
	}


	@Test
	public void shouldAddUserToSuccessListIfDuplicateUsersAreSendInvite() {
		invitationWriter.setInvitationStatus(getInvitationStatusParameters());
		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		when(courseraTokenComponent.postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class), Mockito.any(HttpMethod.class)))
		.thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "DUPLICATE_EXTERNAL_ID".getBytes(), null));

		invitationWriter.write(getActiveUsersList());

		assertEquals(2, invitationWriter.getInvitationStatus().getSuccessfulUserInvite().size());
		verify(fileOpUtils).readAccessToken();
		verify(courseraTokenComponent, times(4)).postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)
				, Mockito.any(HttpMethod.class));
	}

	@Test
	public void shouldAddUserToUnSuccessFulListIfUserDataIsInvalid() {
		invitationWriter.setInvitationStatus(getInvitationStatusParameters());
		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		when(courseraTokenComponent.postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class), Mockito.any(HttpMethod.class)))
		.thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "JSON didn't validate".getBytes(), null));

		invitationWriter.write(getActiveUsersList());

		assertEquals(2, invitationWriter.getInvitationStatus().getUnsuccessfulUserInvite().size());
		verify(fileOpUtils).readAccessToken();
		verify(courseraTokenComponent, times(4)).postOrDeleteInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)
				, Mockito.any(HttpMethod.class));
	}

	@Test
	public void shouldSendEmailToAdmin() {
		invitationWriter.setInvitationStatus(getInvitationStatusParameters());

		invitationWriter.getInvitationStatus().getSuccessfulUserInvite().add("rac123");
		invitationWriter.getInvitationStatus().getSuccessfulUserInvite().add("rac345");
		invitationWriter.getInvitationStatus().getUnsuccessfulUserInvite().put("rac1", "invalid user details");

		when(emailComponent.sendEmailForInviteAPIFailure(Mockito.any(EmailContents.class), Mockito.anyMap())).thenReturn(true);
		invitationWriter.afterStep(stepExecution);

		verify(emailComponent).sendEmailForInviteAPIFailure(Mockito.any(EmailContents.class),  Mockito.anyMap());
	}

	private List<User> getActiveUsersList(){

		List<User> userList = new ArrayList<>();
		User testUser1 = new User();
		testUser1.setExternalId("413861904681");
		testUser1.setFullName("testuser1");
		testUser1.setEmail("testuser1@yash.com");
		testUser1.setStatus("A");

		User testUser2 = new User();
		testUser2.setExternalId("413861904684");
		testUser2.setFullName("");
		testUser2.setEmail("testuser2@yash.com");
		testUser2.setStatus("");

		userList.add(testUser1);
		userList.add(testUser2);

		return userList;
	}

	private List<User> getTerminateUsersList(){

		List<User> userList = new ArrayList<>();
		User testUser1 = new User();
		testUser1.setExternalId("413861904681");
		testUser1.setFullName("testuser1");
		testUser1.setEmail("testuser1@yash.com");
		testUser1.setStatus("T");

		User testUser2 = new User();
		testUser2.setExternalId("413861904684");
		testUser2.setFullName("");
		testUser2.setEmail("testuser2@yash.com");
		testUser2.setStatus("T");

		userList.add(testUser1);
		userList.add(testUser2);

		return userList;
	}

	private List<User> getInvalidUsersList(){

		List<User> userList = new ArrayList<>();
		User testUser1 = new User();
		testUser1.setExternalId("413861904681");
		testUser1.setFullName("testuser1@yash.com");
		testUser1.setEmail("testuser1@yash.com");
		testUser1.setStatus("AT");

		User testUser2 = new User();
		testUser2.setExternalId("413861904684");
		testUser2.setFullName("testuser2");
		testUser2.setEmail("testuser2@yash.com");
		testUser2.setStatus("AT");

		userList.add(testUser1);
		userList.add(testUser2);

		return userList;
	}


	private ApiResponse getApiResponse() {
		ApiResponse response = new ApiResponse();
		Element element = new Element();
		element.setExternalId("413861904681");
		element.setFullName("jd");
		element.setEmail("john@yash.com");
		element.setProgramId("Q0Wzd5osEei1PwqN7iH8Jg");
		element.setId("Q0Wzd5osEei1PwqN7iH8Jg~413861904681");

		List<Element> elements = new ArrayList<>();
		elements.add(element);

		response.setElements(elements);

		return response;
	}

	private List<String> getProgramIdList(){
		List<String> programIds = new ArrayList<>();
		programIds.add("122331");
		programIds.add("123452");

		return programIds;
	}

	private Map<String, String> getTokens(){
		Map<String, String> tokensMap = new HashMap<>();
		tokensMap.put(GlobalConstants.ACCESS_TOKEN_KEY, "accesstoken");
		tokensMap.put(GlobalConstants.REFRESH_TOKEN_KEY, "refreshtoken");

		return tokensMap;
	}

	private InvitationParameters getInvitationStatusParameters() {
		InvitationParameters invitationParameters = new InvitationParameters();
		invitationParameters.setProgramIds(getProgramIdList());
		invitationParameters.setSuccessfulUserInvite(new ArrayList<>());
		invitationParameters.setUnsuccessfulUserInvite(new HashMap<>());

		return invitationParameters;
	}
}
