package com.yash.coursera.integration.config;

import java.net.MalformedURLException;
import java.util.List;

import org.json.JSONObject;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
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

import com.yash.coursera.integration.batch.ResponseProcessor;
import com.yash.coursera.integration.batch.ResponseReader;
import com.yash.coursera.integration.batch.ResponseWriter;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.Elements;
import com.yash.coursera.integration.model.SFLmsMapper;

@Component
public class BatchConfig {

	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Value("${GET_LOCAL_CONTENT_URL}")
	private String localContentApiUrl;

	@Value("${limit.per.batch.read.operation}")
	private Integer limitCountPerRead;

	@Value("${OUTPUT_FILE}")
	private String fileName;

	@Value("${GET_PROGRAM_API}")
	private String getProgramListApi;

	@Value("${GET_CONTENTS_API}")
	private String getContentsApi;
	
	private static String refreshToken;

	@Value("${REFRESH_TOKEN}")
	private String refreshTokenParamValue;

	@Value("${ACCESS_TYPE}")
	private String accessTypeParamValue;

	@Value("${CLIENT_SECRET}")
	private String clientSecret;

	@Value("${CLIENT_ID}")
	private String clientId;

	@Value("${CALLBACK_URI}")
	private  String callBackUri;

	@Value("${AUTHORIZATION_CODE}")
	private String authCodeParamValue;

	@Value("${AUTH_TOKEN_URI}")
	private String getAuthTokenUri;

	@Value("${GET_CODE_URI}")
	private String getCodeUri;

	@Autowired
	private JobBuilderFactory jobs;

	public Job processJob() {
		return jobs.get("processJob").incrementer(new RunIdIncrementer()).flow(getStep()).end().build();
	}

	public Step getStep() {

		Step stepContentApiCall = null;
		try {
			stepContentApiCall = stepBuilderFactory.get(GlobalConstants.STEP_NAME).allowStartIfComplete(false)
					.<Elements, List<SFLmsMapper>>chunk(1).reader(reader()).processor(processor())
					.writer(writer()).build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return stepContentApiCall;
	}

	public ItemReader<Elements> reader() throws MalformedURLException {
		ResponseReader reader = new ResponseReader(localContentApiUrl, GlobalConstants.REQUEST_METHOD, 0, this, limitCountPerRead);
		return reader;
	}

	public ItemProcessor<Elements, List<SFLmsMapper>> processor() {
		return new ResponseProcessor();
	}

	public ItemWriter<List<SFLmsMapper>> writer() {
		ResponseWriter writer = new ResponseWriter();
		writer.setFileName(fileName);
		return writer;
	}

	public JSONObject getAccessToken(String code, RestTemplate restTemplate) {
		String access_token_url = getAuthTokenUri;
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

		ResponseEntity<String> response = restTemplate.exchange(access_token_url, HttpMethod.POST, request,
				String.class);
		String body = response.getBody();
		// System.out.println("Access Token Response ---------" + response.getBody());
		JSONObject jsonObj = new JSONObject(body);

		return jsonObj;
	}

	public String getNewAccessToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add(GlobalConstants.GRANT_TYPE_KEY, refreshTokenParamValue);
		map.add(GlobalConstants.REFRESH_TOKEN_KEY, refreshToken);
		map.add(GlobalConstants.CLIENT_ID_KEY, clientId);
		map.add(GlobalConstants.CLIENT_SECRET_KEY, clientSecret);

		HttpEntity<?> request = new HttpEntity<Object>(map, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(getAuthTokenUri, HttpMethod.POST, request,
				String.class);
		String body = response.getBody();
		JSONObject jsonObj = new JSONObject(body);
		String accessToken = (String) jsonObj.get(GlobalConstants.ACCESS_TOKEN_KEY);
		writeToFile(accessToken, refreshToken);
		return accessToken;
	}
	
	private static void writeToFile(String accessToken, String refreshToken) {
		String[] str = new String[] { GlobalConstants.ACCESS_TOKEN_KEY + "=" + accessToken,
				GlobalConstants.REFRESH_TOKEN_KEY + "=" + refreshToken };
		FileOpUtils.writeToFile(str);
	}


}
