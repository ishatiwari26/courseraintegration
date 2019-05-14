package com.yash.coursera.integration.batch;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private BatchConfig jobConfigurer;

	@Autowired
	private FileOpUtils fileOpUtils;

	HttpHeaders headers = new HttpHeaders();
	RestTemplate restTemplate = new RestTemplate();

	private Integer limitCountPerRead;
	private Integer index = 0;
	private String apiUrl;

	private String accessToken;
	private String refreshToken;

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/*
	 * public BatchConfig getJobConfigurer() { return jobConfigurer; }
	 */

	public void setJobConfigurer(BatchConfig jobConfigurer) {
		this.jobConfigurer = jobConfigurer;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	/*
	 * public int getIndex() { return index; }
	 */

	/*
	 * public void setIndex(int index) { this.index = index; }
	 */

	/*
	 * public Integer getLimitCountPerRead() { return limitCountPerRead; }
	 */

	public void setLimitCountPerRead(Integer limitCountPerRead) {
		this.limitCountPerRead = limitCountPerRead;
	}

	public ResponseReader(BatchConfig jobConfigurer, Integer limitCountPerRead, FileOpUtils fileOpUtils) {
		index = 0;
		this.jobConfigurer = jobConfigurer;
		this.limitCountPerRead = limitCountPerRead;
		this.fileOpUtils = fileOpUtils;
	}

	public ResponseReader() {
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		JobExecution jobExecution = stepExecution.getJobExecution();
		apiUrl = jobExecution.getJobParameters().getString("apiUrl");
	}

	@Override
	public Elements read() throws IOException {

		ApiResponse apiResponse = getContentsList("?start=" + index + "&limit=100");

		Elements elem = null;
		try {
			if (apiResponse != null) {
				List<Element> list = apiResponse.getElements();
				if (!CollectionUtils.isEmpty(list)) {
					elem = new Elements();
					elem.setElement(list);
				}
			}

		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
		return elem;
	}

	private ApiResponse getContentsList(String queryParams) {
		ApiResponse response = null;
		try {
			if (accessToken == null) {
				Map<String, String> tokensMap = fileOpUtils.readAccessToken();
				accessToken = tokensMap.get(GlobalConstants.ACCESS_TOKEN_KEY);
				refreshToken = tokensMap.get(GlobalConstants.REFRESH_TOKEN_KEY);
			}
			if (accessToken != null) {
				response = callContentsAPI(queryParams, accessToken);
			}
		} catch (RestClientException e) {
			try {
				accessToken = jobConfigurer.getNewToken(refreshToken);
				response = callContentsAPI(queryParams, accessToken);
			} catch (RestClientException ex) {
				throw ex;
			}
		}
		return response;
	}

	private ApiResponse callContentsAPI(String queryParams, String accesstoken) {

		headers.set("Authorization", "Bearer " + accesstoken);
		HttpEntity<String> entity = new HttpEntity<>(null, headers);

		ResponseEntity<ApiResponse> response = restTemplate.exchange(apiUrl + queryParams, HttpMethod.GET, entity,
				ApiResponse.class);
		index = index + limitCountPerRead;
		
		
//		response.getBody().setElements(getElements());
		return response.getBody();
	}
	private List<Element>  getElements() {
		List<Element> elements = new ArrayList<>();
		Element elementFirst = new Element();
		elementFirst.setUserId("100009");
		elementFirst.setId("Q0Wzd5osEei1PwqN7iH8Jg");
		elementFirst.setIsCompleted(true);
		elementFirst.setCompletedAt(new Date(System.currentTimeMillis()));
		elementFirst.setGrade("0.91");
		
		Element elementSecond = new Element();
		elementSecond.setUserId("100052");
		elementSecond.setId("lfWYVKVPEeiVoQq-j07Zog");
		elementSecond.setIsCompleted(true);
		elementSecond.setCompletedAt(new Date(System.currentTimeMillis()));
		elementSecond.setGrade("0.92");
		
		Element elementThird = new Element();
		elementThird.setUserId("100083");
		elementThird.setId("cH158posEeiRNRKU687nRg");
		elementThird.setIsCompleted(true);
		elementThird.setCompletedAt(new Date(System.currentTimeMillis()));
		elementThird.setGrade("0.93");
		
		Element elementForth = new Element();
		elementForth.setUserId("100093");
		elementForth.setId("Q0Wzd5osEei1PwqN7iH8Jg");
		elementForth.setIsCompleted(true);
		elementForth.setCompletedAt(new Date(System.currentTimeMillis()));
		elementForth.setGrade("0.94");
		
		elements.add(elementFirst);
		elements.add(elementSecond);
		elements.add(elementThird);
		elements.add(elementForth);
		return elements;
	}

}
