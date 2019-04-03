package com.yash.coursera.integration.batch;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.ApiResponse;
import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.Elements;

@Component
public class ResponseReader implements ItemReader<Elements> {

	private Integer limitCountPerRead;
	private Integer index = 0;
	private String apiUrl;
	private String requestMethod;
	private Integer jobCount;
	private ApiResponse apiResponse;
	private BatchConfig jobConfigurer;
	private JobExecution jobExecution;
	HttpHeaders headers = new HttpHeaders();
	RestTemplate restTemplate = new RestTemplate();
	private String accessToken, refreshToken;
	
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Integer getLimitCountPerRead() {
		return limitCountPerRead;
	}

	public void setLimitCountPerRead(Integer limitCountPerRead) {
		this.limitCountPerRead = limitCountPerRead;
	}

	public ResponseReader(String requestMethod, Integer jobCount, BatchConfig jobConfigurer, Integer limitCountPerRead) {
		this.requestMethod = requestMethod;
		this.jobCount = jobCount;
		index = 0;
		this.jobConfigurer = jobConfigurer;
		this.limitCountPerRead = limitCountPerRead;
	}

	public ResponseReader() {
	}
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		jobExecution = stepExecution.getJobExecution();
		apiUrl = jobExecution.getJobParameters().getString("apiUrl");
	}
	
	@Override
	public Elements read() throws IOException {

		apiResponse = getContentsList("?start=" + index + "&limit=100");

		Elements elem = null;
		try {
			List<Element> list = apiResponse.getElements();
			if (!CollectionUtils.isEmpty(list)) {
				elem = new Elements();
				elem.setElement(list);
			}
			jobCount++;

		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
		return elem;
	}
	
	public ApiResponse getContentsList(String queryParams) {
		ApiResponse response = null;
		Map<String, String> tokensMap = FileOpUtils.readAccessToken();
		try {
			if (accessToken == null) {
				accessToken = tokensMap.get(GlobalConstants.ACCESS_TOKEN_KEY);
				refreshToken = tokensMap.get(GlobalConstants.REFRESH_TOKEN_KEY);
			}

			if (accessToken != null) {
				response = callContentsAPI(queryParams,tokensMap.get(GlobalConstants.ACCESS_TOKEN_KEY));
			}
			/*Map<String, String> tokensMap = FileOpUtils.readAccessToken();
			if(!tokensMap.isEmpty()){
				accessToken = tokensMap.get(GlobalConstants.ACCESS_TOKEN_KEY);
				refreshToken = tokensMap.get(GlobalConstants.REFRESH_TOKEN_KEY);
				response = callContentsAPI(queryParams);
			}*/
		} catch (RestClientException e) {
			try {
				accessToken = jobConfigurer.getNewToken(tokensMap.get(GlobalConstants.REFRESH_TOKEN_KEY));
				response = callContentsAPI(queryParams,accessToken);
			} catch (RestClientException ex) {
				// to cover condition if exception occurs in new access token generation through
				// refresh token itself
				throw ex;
			}

		}
		return response;
	}
	
	public ApiResponse callContentsAPI(String queryParams,String accesstoken) {
		
		headers.set("Authorization", "Bearer " + accesstoken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		ResponseEntity<ApiResponse> response = restTemplate.exchange(apiUrl + queryParams, HttpMethod.GET, entity, ApiResponse.class);
		
		index = index + limitCountPerRead;
		return response.getBody();
	}


}
