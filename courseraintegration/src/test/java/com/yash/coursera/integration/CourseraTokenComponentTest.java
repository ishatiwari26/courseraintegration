package com.yash.coursera.integration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.components.CourseraTokenComponent;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.ApiResponse;
import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.User;

@RunWith(MockitoJUnitRunner.class)
public class CourseraTokenComponentTest {

	@InjectMocks
	CourseraTokenComponent courseraComponent;

	@Mock
	FileOpUtils commonUtils;

	@Mock
	RestTemplate restTemplate;


	@Before
	public void setUp() {
		ReflectionTestUtils.setField(courseraComponent, "getAuthTokenUri",
				"https://accounts.coursera.org/oauth2/v1/token");
		ReflectionTestUtils.setField(courseraComponent, "getProgramListApi",
				"https://api.coursera.org/api/businesses.v1/9SIv6szMSVCAU_Gv8qycvw/programs");
		ReflectionTestUtils.setField(courseraComponent, "getContentsApi",
				"https://api.coursera.org/api/businesses.v1/9SIv6szMSVCAU_Gv8qycvw/contents");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGenerate_AccessToken() throws Exception {
		String code = "testCode";
		JSONObject tokenJSON = new JSONObject();
		tokenJSON.put("access_token", "testAccessToken");
		tokenJSON.put("refresh_token", "testRefreshToken");
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		Mockito.when(response.getBody()).thenReturn(tokenJSON.toString());
		Mockito.mock(MultiValueMap.class);
		Mockito.mock(HttpEntity.class);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(response);
		courseraComponent.getAccessToken(code, restTemplate);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGenerate_NewAccessToken_ByRefreshToken() throws Exception {
		String refreshToken = "testRefreshToken";
		JSONObject tokenJSON = new JSONObject();
		tokenJSON.put("access_token", "testAccessToken");
		tokenJSON.put("refresh_token", "testRefreshToken");
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		Mockito.when(response.getBody()).thenReturn(tokenJSON.toString());
		Mockito.mock(MultiValueMap.class);
		Mockito.mock(HttpEntity.class);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(response);
		doNothing().when(commonUtils).writeToFile(Mockito.anyObject());
		assertEquals(tokenJSON.get("access_token"),courseraComponent.getNewAccessToken(refreshToken));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldCallProgramsAPI() {
		String accessToken = "testAccessToken";
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		Mockito.mock(HttpEntity.class);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(response);
		assertEquals(response,courseraComponent.callProgramsAPI(0, 0, accessToken));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldCallContentsAPI() {
		String accessToken = "testAccessToken";
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		Mockito.mock(HttpEntity.class);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(response);
		assertEquals(response,courseraComponent.callContentsAPI(0, 0, accessToken));
	}
	
	@Test
	public void shouldPostUserInvitation(){
		String accessToken = "testAccessToken";
		String programId = "Q0Wzd5osEei1PwqN7iH8Jg";
		
		User testUser = getTestUser();
		testUser.setStatus(GlobalConstants.ACTIVE_STATUS);
		
		ResponseEntity<ApiResponse> response = new ResponseEntity<ApiResponse>(getApiResponse(), HttpStatus.CREATED);
		when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), ArgumentMatchers.eq(ApiResponse.class))).thenReturn(response);
		
		ResponseEntity<ApiResponse> actualResponse = courseraComponent.postOrDeleteInvitation(programId, accessToken, testUser, HttpMethod.POST);
		
		assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
	}
	
	@Test
	public void shouldDeleteUserInvitation(){
		String accessToken = "testAccessToken";
		String programId = "Q0Wzd5osEei1PwqN7iH8Jg";
		
		User testUser = getTestUser();
		testUser.setStatus(GlobalConstants.TERMINATE_STATUS);
		
		ResponseEntity<ApiResponse> response = new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), ArgumentMatchers.eq(ApiResponse.class))).thenReturn(response);
		
		ResponseEntity<ApiResponse> actualResponse = courseraComponent.postOrDeleteInvitation(programId, accessToken, testUser, HttpMethod.DELETE);
		
		assertEquals(null, actualResponse.getBody());
		assertEquals(HttpStatus.NO_CONTENT, actualResponse.getStatusCode());
	}
	
	private User getTestUser() {
		User testUser = new User();
		testUser.setExternalId("413861904681");
		testUser.setFullName("testuser1@yash.com");
		testUser.setEmail("testuser1@yash.com");
		
		return testUser;
	}
	
	private ApiResponse getApiResponse() {
		ApiResponse response = new ApiResponse();
		Element element = new Element();
		element.setExternalId("413861904681");
		element.setFullName("testuser1@yash.com");
		element.setEmail("testuser1@yash.com");
		element.setProgramId("Q0Wzd5osEei1PwqN7iH8Jg");
		element.setId("Q0Wzd5osEei1PwqN7iH8Jg~413861904681");

		List<Element> elements = new ArrayList<>();
		elements.add(element);

		response.setElements(elements);

		return response;
	}
	
}
