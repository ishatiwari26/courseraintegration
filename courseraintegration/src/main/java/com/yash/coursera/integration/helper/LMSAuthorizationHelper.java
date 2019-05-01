package com.yash.coursera.integration.helper;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Base64;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LMSAuthorizationHelper {

	private final Logger logger = LoggerFactory.getLogger(LMSAuthorizationHelper.class);

	LocalDateTime authTokenExpirationTime = null;

	@Value("${lms.oauth.clientId}")
	private String clientid;

	@Value("${lms.oauth.clientsecret}")
	private String clientsecret;

	@Value("${lms.oauth.userid}")
	private String userid;

	@Value("${lms.oauth.grant_type}")
	private String grantType;

	@Value("${lms.oauth.companyid}")
	private String companyid;

	@Value("${lms.oauth.usertype}")
	private String usertype;

	@Value("${lms.oauth.resourcetype}")
	private String resourcetype;

	@Value("${lms.oauth.uri}")
	String uri;
	RestTemplate restTemplate = new RestTemplate();

	public String getLMSAuthToken() {

		authTokenExpirationTime = LocalDateTime.now();

		String clientCreds = clientid + ":" + clientsecret;

		String base64encodedString = "";
		try {
			base64encodedString = Base64.getEncoder().encodeToString(clientCreds.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(GlobalConstants.AUTHORIZATION_KEY, "Basic " + base64encodedString);
		logger.info("base64encodedString>>" + base64encodedString);

		JSONObject scope = new JSONObject();
		scope.put(GlobalConstants.USER_ID_KEY, userid);
		scope.put(GlobalConstants.COMPANY_ID_KEY, companyid);
		scope.put(GlobalConstants.USER_TYPE_KEY, usertype);
		scope.put(GlobalConstants.RESOURCE_TYPE_KEY, resourcetype);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.GRANT_TYPE_KEY, grantType);
		request.put(GlobalConstants.SCOPE, scope);
		logger.info("request.toString()>>" + request.toString());
		HttpEntity<?> entity = new HttpEntity<Object>(request.toString(), headers);

		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		String body = response.getBody();
		String accessToken = null;
		try {
			JSONObject jsonObj = new JSONObject(body);
			accessToken = (String) jsonObj.get(GlobalConstants.ACCESS_TOKEN_KEY);
			authTokenExpirationTime.plusSeconds((Long) jsonObj.get(GlobalConstants.EXPIRES_IN));
		} catch (Exception e) {

		}
		return accessToken;

	}

	public boolean isLMSAuthTokenValid() {
		boolean isTokenValid = false;
		if (authTokenExpirationTime != null)
			isTokenValid = authTokenExpirationTime.isAfter(LocalDateTime.now());
		logger.info("isTokenValid>>" + isTokenValid);
		return isTokenValid;
	}

}
