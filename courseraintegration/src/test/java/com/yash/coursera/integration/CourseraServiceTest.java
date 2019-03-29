package com.yash.coursera.integration;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.helper.CommonUtils;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.service.CourseraService;

@RunWith(MockitoJUnitRunner.class)
public class CourseraServiceTest {

	@InjectMocks
	CourseraService courseraService;

	@Mock
	CommonUtils commonUtils;

	@Mock
	BatchConfig config;

	@Mock
	RestTemplate restTemplate;

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(courseraService, "getAuthTokenUri","https://accounts.coursera.org/oauth2/v1/token");
	}

	@Test
	public void shouldGenerate_AccessToken() throws Exception {
		String code = "testCode";
		JSONObject tokenJSON = new JSONObject();
		tokenJSON.put("access_token", "testAccessToken");
		tokenJSON.put("refresh_token", "testRefreshToken");
		ResponseEntity<String> response =Mockito.mock(ResponseEntity.class);
		Mockito.when(response.getBody()).thenReturn(tokenJSON.toString());
		
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		MultiValueMap<String, String> map = Mockito.mock(MultiValueMap.class);
		HttpEntity<MultiValueMap<String, String>> request = Mockito.mock(HttpEntity.class);
		
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(response);
		courseraService.getAccessToken(code, restTemplate);
	}
	@Test
	public void shouldGenerate_NewAccessToken_ByRefreshToken() throws Exception {
		String refreshToken = "testRefreshToken";
		JSONObject tokenJSON = new JSONObject();
		tokenJSON.put("access_token", "testAccessToken");
		tokenJSON.put("refresh_token", "testRefreshToken");
		ResponseEntity<String> response =Mockito.mock(ResponseEntity.class);
		Mockito.when(response.getBody()).thenReturn(tokenJSON.toString());
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		MultiValueMap<String, String> map = Mockito.mock(MultiValueMap.class);
		HttpEntity<?> request = Mockito.mock(HttpEntity.class);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(response);
		doNothing().when(commonUtils).writeToFile(Mockito.anyString(), Mockito.anyString());
		courseraService.getNewAccessToken(refreshToken);
	}
}
