package com.yash.coursera.integration.components;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.ApiResponse;
import com.yash.coursera.integration.model.User;

@Component
public class CourseraTokenComponent {

	@Value("${program.api}")
	private String getProgramListApi;

	@Value("${content.api}")
	private String getContentsApi;

	@Value("${refresh.token}")
	private String refreshTokenParamValue;

	@Value("${auth.access.type}")
	private String accessTypeParamValue;

	@Value("${client.secret}")
	private String clientSecret;

	@Value("${client.id}")
	private String clientId;

	@Value("${callback.uri}")
	private String callBackUri;

	@Value("${authorization.code}")
	private String authCodeParamValue;

	@Value("${auth.token.uri}")
	private String getAuthTokenUri;

	@Value("${invitation.api}")
	private String inviteApiUrl;

	private String accessToken, refreshToken;
	
	@Autowired
	FileOpUtils commonUtils;

	
	RestTemplate restTemplate = new RestTemplate();
	

	public JSONObject getAccessToken(String code, RestTemplate restTemplate) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
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

	public String getNewAccessToken(String refreshToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add(GlobalConstants.GRANT_TYPE_KEY, refreshTokenParamValue);
		map.add(GlobalConstants.REFRESH_TOKEN_KEY, refreshToken);
		map.add(GlobalConstants.CLIENT_ID_KEY, clientId);
		map.add(GlobalConstants.CLIENT_SECRET_KEY, clientSecret);

		HttpEntity<?> request = new HttpEntity<Object>(map, headers);

		ResponseEntity<String> response = restTemplate.exchange(getAuthTokenUri, HttpMethod.POST, request,
				String.class);
		String body = response.getBody();
		JSONObject jsonObj = new JSONObject(body);
		accessToken = (String) jsonObj.get(GlobalConstants.ACCESS_TOKEN_KEY);
		commonUtils.writeToFile(new String[] { GlobalConstants.ACCESS_TOKEN_KEY + "=" + accessToken,
				GlobalConstants.REFRESH_TOKEN_KEY + "=" + refreshToken });
		return accessToken;
	}

	public ResponseEntity<String> callProgramsAPI(Integer start, Integer limit, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		String queryParams = "?" + GlobalConstants.START + "=" + start + "&" + GlobalConstants.LIMIT + "=" + limit;

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(getProgramListApi + queryParams, HttpMethod.GET, entity,
				String.class);
		return response;
	}

	public ResponseEntity<String> callContentsAPI(Integer start, Integer limit, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		String queryParams = "?" + GlobalConstants.START + "=" + start + "&" + GlobalConstants.LIMIT + "=" + limit;

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(getContentsApi + queryParams, HttpMethod.GET, entity,
				String.class);
		return response;
	}

	public ResponseEntity<ApiResponse> postOrDeleteInvitation(String programId, String accessToken, User user, HttpMethod requestMethod){
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String url = inviteApiUrl + programId + "/invitations";
		
		if(requestMethod == HttpMethod.DELETE) {
			url = url + "/" + programId + "~" + user.getExternalId();
		}
		
		HttpEntity<User> entity = new HttpEntity<User>(user, headers);
		
		Long startTime = System.currentTimeMillis(); 
		ResponseEntity<ApiResponse> response = restTemplate.exchange(url, requestMethod, entity, ApiResponse.class);
		Long endTime = System.currentTimeMillis();
		
		System.out.println(response);
		System.out.println("Total time to get response :  " + (endTime-startTime) + " milliseconds");
		return response;
	}

}
