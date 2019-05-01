package com.yash.coursera.integration.helper;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

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
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class LMSAuthorizationHelperTest {
	
	@InjectMocks
	LMSAuthorizationHelper helper;

	@Mock
	RestTemplate restTemplate;
	
	@Before
	public void setUp() {
		ReflectionTestUtils.setField(helper, "uri",
				"https://partlms0037.scdemo.successfactors.com/learning/oauth-api/rest/v1/token");
		ReflectionTestUtils.setField(helper, "authTokenExpirationTime",
				LocalDateTime.now());
		
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldCallGetLMSAuthToken() {
		String accessToken = "accessToken";
		Long expiresIn = 1800L;
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(GlobalConstants.ACCESS_TOKEN_KEY, accessToken);
		jsonObj.put(GlobalConstants.EXPIRES_IN, expiresIn);
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		
		Mockito.mock(HttpEntity.class);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(response);
		Mockito.when(response.getBody()).thenReturn(jsonObj.toString());
		assertEquals(accessToken,helper.getLMSAuthToken());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldCallisLMSAuthTokenValid() {
		assertEquals(false,helper.isLMSAuthTokenValid());
	}

}
