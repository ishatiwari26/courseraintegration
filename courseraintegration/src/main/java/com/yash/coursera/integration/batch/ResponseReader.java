package com.yash.coursera.integration.batch;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
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

	private int index = 0;

	private String apiUrl;
	private String requestMethod;
	private Integer jobCount;
	private ApiResponse apiResponse;

	BatchConfig jobConfigurer;

	private String accessToken, refreshToken;

	public ResponseReader(String apiUrl, String requestMethod, Integer jobCount, BatchConfig jobConfigurer, Integer limitCountPerRead) {
		this.apiUrl = apiUrl;
		this.requestMethod = requestMethod;
		this.jobCount = jobCount;
		index = 0;
		this.jobConfigurer = jobConfigurer;
		this.limitCountPerRead = limitCountPerRead;
	}

	public ResponseReader() {
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

	private ApiResponse callContentsAPI(String queryParams) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("URL>>>"+apiUrl + queryParams);
		ResponseEntity<ApiResponse> response = restTemplate.exchange(apiUrl + queryParams, HttpMethod.GET, entity,
				ApiResponse.class);
		index = index + limitCountPerRead;
		return response.getBody();
	}

	public ApiResponse getContentsList(String queryParams) {
		// System.out.println("newAccessToken>>" + accessToken);
		ApiResponse response = null;
		try {
			if (accessToken == null) {
				Map<String, String> tokensMap = FileOpUtils.readAccessToken();
				accessToken = tokensMap.get(GlobalConstants.ACCESS_TOKEN_KEY);
				refreshToken = tokensMap.get(GlobalConstants.REFRESH_TOKEN_KEY);
			}

			if (accessToken != null) {
				response = callContentsAPI(queryParams);
			}

		} catch (RestClientException e) {
			try {
				accessToken = jobConfigurer.getNewAccessToken();
				response = callContentsAPI(queryParams);
			} catch (RestClientException ex) {
				// to cover condition if exception occurs in new access token generation through
				// refresh token itsel
			}

		}
		return response;
	}

}
