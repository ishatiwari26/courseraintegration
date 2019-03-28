package com.yash.coursera.integration.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.helper.CommonUtils;
import com.yash.coursera.integration.helper.GlobalConstants;

@Service
public class CourseraService {

	@Value("${GET_PROGRAM_API}")
	private String getProgramListApi;

	@Value("${GET_CONTENTS_API}")
	private String getContentsApi;

	@Value("${REFRESH_TOKEN}")
	private String refreshTokenParamValue;

	@Value("${ACCESS_TYPE}")
	private String accessTypeParamValue;

	@Value("${CLIENT_SECRET}")
	private String clientSecret;

	@Value("${CLIENT_ID}")
	private String clientId;

	@Value("${CALLBACK_URI}")
	private String callBackUri;

	@Value("${AUTHORIZATION_CODE}")
	private String authCodeParamValue;

	@Value("${AUTH_TOKEN_URI}")
	private String getAuthTokenUri;

	String accessToken, refreshToken; 
	
	CommonUtils commonUtils=new CommonUtils();	
	
	public JSONObject getAccessToken(String code, RestTemplate restTemplate) {
		HttpHeaders headers = new HttpHeaders();
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		map.add(GlobalConstants.CODE_KEY, code);
		map.add(GlobalConstants.GRANT_TYPE_KEY, authCodeParamValue);
		map.add(GlobalConstants.REDIRECT_URI_KEY, callBackUri);
		map.add(GlobalConstants.CLIENT_ID_KEY, clientId);
		map.add(GlobalConstants.CLIENT_SECRET_KEY, clientSecret);
		map.add(GlobalConstants.ACCESS_TYPE_KEY, accessTypeParamValue);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response = restTemplate.exchange(getAuthTokenUri, HttpMethod.POST, request,
				String.class);
		String body = response.getBody();
		JSONObject jsonObj = new JSONObject(body);

		return jsonObj;
	}
	
	public String getNewAccessToken() {
		String access_token_url = getAuthTokenUri;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		map.add(GlobalConstants.GRANT_TYPE_KEY, refreshTokenParamValue);
		map.add(GlobalConstants.REFRESH_TOKEN_KEY, refreshToken);
		map.add(GlobalConstants.CLIENT_ID_KEY, clientId);
		map.add(GlobalConstants.CLIENT_SECRET_KEY, clientSecret);

		HttpEntity<?> request = new HttpEntity<Object>(map, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(access_token_url, HttpMethod.POST, request,
				String.class);
		String body = response.getBody();
		JSONObject jsonObj = new JSONObject(body);
		accessToken = (String) jsonObj.get(GlobalConstants.ACCESS_TOKEN_KEY);
		commonUtils.writeToFile(accessToken, refreshToken);
		return accessToken;
	}
	
	public ResponseEntity<String> callProgramsAPI(Integer start, Integer limit, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		String url = getProgramListApi;
		String queryParams = "?" + GlobalConstants.START + "=" + start + "&" + GlobalConstants.LIMIT + "=" + limit;

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url + queryParams, HttpMethod.GET, entity,
				String.class);
		return response;
	}
	public ResponseEntity<String> callContentsAPI(Integer start, Integer limit, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		String url = getContentsApi;
		String queryParams = "?" + GlobalConstants.START + "=" + start + "&" + GlobalConstants.LIMIT + "=" + limit;

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url + queryParams, HttpMethod.GET, entity,
				String.class);
		return response;
	}

}
