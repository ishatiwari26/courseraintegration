package com.yash.coursera.integration.batch;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.StepExecution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import com.yash.coursera.integration.components.CourseraComponent;
import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.dao.CourseraAPIDataDao;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.ApiResponse;
import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.Elements;
import com.yash.coursera.integration.model.User;

@RunWith(SpringRunner.class)
public class InviteProcessorTest {

	@InjectMocks
	private InviteProcessor inviteProcessor;

	@Mock
	private BatchConfig jobConfigurer;

	@Mock
	private CourseraAPIDataDao dao;

	@Mock
	private CourseraComponent courseraComponent;

	@Mock
	private StepExecution stepExecution;

	@Mock
	private FileOpUtils fileOpUtils;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private List<String> programIds;

	@Test
	public void shouldReturnNullIfProgramListIsEmpty() throws Exception {
		programIds = new ArrayList<>();
		inviteProcessor.setProgramIds(programIds);

		assertEquals(null, inviteProcessor.process(getUser()));
	}

	@Test
	public void shouldExecuteBeforeSetup() {
		when(dao.getProgramIds()).thenReturn(programIds);
		inviteProcessor.beforeStep(stepExecution);
		verify(dao).getProgramIds();
	}

	@Test
	public void shouldReturnNullIfInvitationApiStatusIsNull() throws Exception {
		inviteProcessor.setProgramIds(getProgramIdList());
		ResponseEntity<ApiResponse> response = new ResponseEntity<ApiResponse>((ApiResponse)null, HttpStatus.CREATED);

		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		when(courseraComponent.postInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class))).thenReturn(response);

		assertEquals(null, inviteProcessor.process(getUser()));
	}

	@Test
	public void shouldThrowExceptionIfNotAuthorized() throws Exception {
		String accessToken = "accessToken";

		inviteProcessor.setProgramIds(getProgramIdList());

		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		doThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED)).when(courseraComponent).postInvitation(Mockito.any(String.class), 
				Mockito.any(String.class), Mockito.any(User.class));
		when(jobConfigurer.getNewToken(Mockito.any(String.class))).thenReturn(accessToken);

		exceptionRule.expect(HttpClientErrorException.class);
		exceptionRule.expectMessage(containsString("401 UNAUTHORIZED"));
		inviteProcessor.process(getUser());
	}

	@Test
	public void shouldReturnResponseIfInvitationApiStatusIsCreated() throws Exception {
		inviteProcessor.setProgramIds(getProgramIdList());

		ResponseEntity<ApiResponse> response = new ResponseEntity<ApiResponse>(getApiResponse(), HttpStatus.CREATED);
		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		when(courseraComponent.postInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class))).thenReturn(response);

		Elements actualResponse = inviteProcessor.process(getUser());

		assertEquals("413861904681", actualResponse.getElement().get(0).getExternalId());
		assertEquals("Q0Wzd5osEei1PwqN7iH8Jg~413861904681", actualResponse.getElement().get(0).getId());
	}

	@Test
	public void shouldReturnNullIfUserIsAlreadyInvited() throws Exception {
		String accessToken = "accessToken";

		inviteProcessor.setProgramIds(getProgramIdList());

		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(courseraComponent).postInvitation(Mockito.any(String.class), 
				Mockito.any(String.class), Mockito.any(User.class));
		when(jobConfigurer.getNewToken(Mockito.any(String.class))).thenReturn(accessToken);

		assertEquals(null, inviteProcessor.process(getUser()));
	}

	@Test
	public void shouldReturnResponseIfNewTokenGeneratedUsingRefreshToken() throws Exception {
		String accessToken = "newAccessToken";

		inviteProcessor.setProgramIds(getProgramIdList());
		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		ResponseEntity<ApiResponse> response = new ResponseEntity<ApiResponse>(getApiResponse(), HttpStatus.CREATED);
		when(courseraComponent.postInvitation(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(User.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED))
				.thenReturn(response);

		when(jobConfigurer.getNewToken(Mockito.any(String.class))).thenReturn(accessToken);
		Elements actualResponse = inviteProcessor.process(getUser());

		assertEquals("413861904681", actualResponse.getElement().get(0).getExternalId());
		assertEquals("Q0Wzd5osEei1PwqN7iH8Jg~413861904681", actualResponse.getElement().get(0).getId());	
	}

	private User getUser() {
		User user = new User();
		user.setId(1);
		user.setExternalId("413861904681");
		user.setFullName("john@yash.com");
		user.setEmail("jd@yash.com");

		return user;
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

}
