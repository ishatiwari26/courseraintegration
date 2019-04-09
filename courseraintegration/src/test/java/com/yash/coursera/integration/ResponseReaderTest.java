package com.yash.coursera.integration;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.formula.atp.AnalysisToolPak;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.batch.ResponseReader;
import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.model.ApiResponse;
import com.yash.coursera.integration.model.Element;

@RunWith(MockitoJUnitRunner.class)
public class ResponseReaderTest {

	@InjectMocks
	ResponseReader responseReader;
	@Mock
	private ApiResponse apiResponse;
	@Mock
	private BatchConfig jobConfigurer;
	@Mock
	private JobExecution jobExecution;
	@Mock
	private StepExecution stepExecution;
	@Mock
	private JobParameters jobParameters;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private HttpHeaders headers;
	
	/*@Mock
	private CollectionUtils coll;*/


	@Before
	public void setUp() {
		/*Mockito.when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		Mockito.when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		Mockito.when(jobParameters.getString(Mockito.anyString())).thenReturn("https://api.coursera.org/api/businesses.v1/9SIv6szMSVCAU_Gv8qycvw/contents");*/
		responseReader.setRestTemplate(restTemplate);
	}

	/*@Test
	public void shouldReadDataFromCourseraAPI() throws IOException {
		Element element = new Element();
		element.setContentId("testContentId");
		element.setContentType("testContentType");
		List<Element> listOfElements = new ArrayList<>();
		listOfElements.add(element);
//		Mockito.when(CollectionUtils.isEmpty(listOfElements)).thenReturn(true);
		Mockito.when(responseReader.getContentsList(Mockito.anyString())).thenReturn(apiResponse);
		Mockito.when(apiResponse.getElements()).thenReturn(listOfElements);
		Assert.assertEquals(listOfElements, responseReader.read());
	}*/

	/*@Test
	public void shouldGetContentsList() {
		Mockito.when(responseReader.callContentsAPI(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(apiResponse);
		responseReader.getContentsList(Mockito.anyString());
	}*/

	@Test
	public void shouldCallContentsAPI() {
		Element element = new Element();
		element.setContentId("testContentId");
		element.setContentType("testContentType");
		List<Element> listOfElements = new ArrayList<>();
		listOfElements.add(element);
		ApiResponse apiGetResponse = new ApiResponse();
		apiGetResponse.setElements(listOfElements);
		
		ResponseEntity<ApiResponse> response = new ResponseEntity(apiGetResponse, HttpStatus.OK);
		
		responseReader.setLimitCountPerRead(1);
				
		when(restTemplate.exchange(Mockito.anyString(),  Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), 
				ArgumentMatchers.eq(ApiResponse.class))).thenReturn(response);

		Assert.assertEquals(response.getBody(), responseReader.callContentsAPI("testParameter", "testAccessToken"));
		
	}

}
