package com.yash.coursera.integration;

import static org.mockito.Mockito.doNothing;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.components.CourseraComponent;
import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.helper.CommonUtils;
import com.yash.coursera.integration.model.User;

@RunWith(MockitoJUnitRunner.class)
public class CourseraComponentTest {

	@InjectMocks
	CourseraComponent courseraComponent;

	@Mock
	CommonUtils commonUtils;

	@Mock
	BatchConfig config;

	@Mock
	RestTemplate restTemplate;

	@Mock
	HttpHeaders headers;

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
		doNothing().when(commonUtils).writeToFile(Mockito.anyString(), Mockito.anyString());
		courseraComponent.getNewAccessToken(refreshToken);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldCallProgramsAPI() {
		String accessToken = "testAccessToken";
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		Mockito.mock(HttpEntity.class);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(response);
		courseraComponent.callProgramsAPI(Mockito.anyInt(), Mockito.anyInt(), accessToken);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldCallContentsAPI() {
		String accessToken = "testAccessToken";
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		Mockito.mock(HttpEntity.class);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(response);
		courseraComponent.callContentsAPI(Mockito.any(Integer.class), Mockito.any(Integer.class), accessToken);
	}
	@SuppressWarnings("unchecked")
	@Test
	public void shouldPostUserInvitation(){
		String accessToken = "testAccessToken";
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		Mockito.mock(HttpEntity.class);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(response);
		courseraComponent.postInvitation(Mockito.any(String.class), accessToken, Mockito.any(User.class), Mockito.any(String.class));
	}
}
