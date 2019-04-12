package com.yash.coursera.integration.batch;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.ApiResponse;
import com.yash.coursera.integration.model.Content;
import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.Elements;
import com.yash.coursera.integration.model.Instructor;
import com.yash.coursera.integration.model.Program;

@RunWith(SpringRunner.class)
public class ResponseReaderTest {

	@InjectMocks
	private ResponseReader responseReader;

	@Mock
	private BatchConfig jobConfigurer;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private StepExecution stepExecution;

	@Mock
	private JobExecution jobExecution;

	@Mock
	private FileOpUtils fileOpUtils;

	@Mock
	private JobParameters jobParameters;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Before
	public void setUp() {
		responseReader.setRestTemplate(restTemplate);
		responseReader.setJobConfigurer(jobConfigurer);
		responseReader.setIndex(0);

		StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
		stepExecution.getJobExecution().getExecutionContext().put("apiUrl", "testApiUrl");
		responseReader.beforeStep(stepExecution);
	}

	@Test
	public void shouldCallReader() throws IOException {
		ResponseEntity<ApiResponse> response = new ResponseEntity<ApiResponse>(getApiResponse(), HttpStatus.OK);
		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
				ArgumentMatchers.eq(ApiResponse.class))).thenReturn(response);
		responseReader.setLimitCountPerRead(1);
		assertNotNull(responseReader.read());
	}

	@Test
	public void shouldReturnNull_WhenTokenIsEmpty() throws IOException {
		when(fileOpUtils.readAccessToken()).thenReturn(getNullTokens());
		assertEquals(null, responseReader.read());
	}

	@Test
	public void shouldThrowRestClientException_WhenTokenExpired() throws IOException {
		String accessToken = "accessToken";
		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		doThrow(new RestClientException("401 UNAUTHORIZED")).when(restTemplate).exchange(Mockito.anyString(),
				Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), ArgumentMatchers.eq(ApiResponse.class));
		when(jobConfigurer.getNewToken(Mockito.any(String.class))).thenReturn(accessToken);
		exceptionRule.expect(RestClientException.class);
		exceptionRule.expectMessage(containsString("401 UNAUTHORIZED"));
		responseReader.read();
	}

	@Test
	public void shouldCallRead_WhenNewTokenGeneratedUsingRefreshToken() throws IOException {
		String accessToken = "newAccessToken";
		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		ResponseEntity<ApiResponse> response = new ResponseEntity<ApiResponse>(getApiResponse(), HttpStatus.OK);
		when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
				ArgumentMatchers.eq(ApiResponse.class))).thenThrow(new RestClientException("401 UNAUTHORIZED"))
						.thenReturn(response);
		when(jobConfigurer.getNewToken(Mockito.any(String.class))).thenReturn(accessToken);
		responseReader.setLimitCountPerRead(1);
		Elements actualResponse = responseReader.read();
		assertEquals("testContentId", actualResponse.getElement().get(0).getContentId());
		assertEquals("testContentType", actualResponse.getElement().get(0).getContentType());
	}

	@Test
	public void shouldReturnNullElements_WhenInvitationApiStatusIsNull() throws Exception {
		responseReader.setLimitCountPerRead(1);
		ResponseEntity<ApiResponse> response = new ResponseEntity<ApiResponse>((ApiResponse) null, HttpStatus.OK);
		when(fileOpUtils.readAccessToken()).thenReturn(getTokens());
		when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
				ArgumentMatchers.eq(ApiResponse.class))).thenReturn(response);
		assertEquals(null, responseReader.read());
	}

	private ApiResponse getApiResponse() {
		Element element = new Element();
		element.setContentId("testContentId");
		element.setContentType("testContentType");
		element.setId("testId");
		element.setTagline("testTagline");
		element.setName("testName");
		element.setUrl("testUrl");
		element.setPartners("testPartners");
		element.setDescription("testDescription");
		element.setLanguageCode("testLanguageCode");
		element.setFullName("testFullName");
		element.setExternalId("testExternalId");
		element.setEmail("testEmail");
		element.setUserId("testUserId");
		element.setIsCompleted(false);
		element.setCompletedAt(new Date(System.currentTimeMillis()));
		element.setGrade("testGrade");
		
		Instructor instructor=new Instructor();
		instructor.setDepartment("testDepartment");
		instructor.setName("testInstructorName");
		instructor.setPhotoUrl("http://testImage.img");
		instructor.setTitle("textTitle");
		
		List<Instructor> listOfInstructor=new ArrayList<>();
		listOfInstructor.add(instructor);
		
		Content content=new Content();
		content.setContentId("testContentID");
		content.setContentType("testContentType");
		
		List<Content> listOfContent=new ArrayList<>();
		listOfContent.add(content);
		Program program=new Program();
		program.setContentUrl("http://testContentUrl.com");
		program.setProgramId("testProgramId");
		List<Program> listOfProgram =new ArrayList<>();
		listOfProgram.add(program);
		
		Object extraMetadata=new Object();
		Object partners=new Object();
		
		element.setInstructors(listOfInstructor);
		element.setContentIds(listOfContent);
		element.setPrograms(listOfProgram);
		element.setExtraMetadata(extraMetadata);
		element.setPartners(partners);
		
		
		List<Element> listOfElements = new ArrayList<>();
		listOfElements.add(element);
		
		ApiResponse apiGetResponse = new ApiResponse();
		apiGetResponse.setElements(listOfElements);
		return apiGetResponse;
	}

	private Map<String, String> getTokens() {
		Map<String, String> tokensMap = new HashMap<>();
		tokensMap.put(GlobalConstants.ACCESS_TOKEN_KEY, "accesstoken");
		tokensMap.put(GlobalConstants.REFRESH_TOKEN_KEY, "refreshtoken");

		return tokensMap;
	}

	private Map<String, String> getNullTokens() {
		Map<String, String> tokensMap = new HashMap<>();
		tokensMap.put(GlobalConstants.ACCESS_TOKEN_KEY, null);
		tokensMap.put(GlobalConstants.REFRESH_TOKEN_KEY, null);

		return tokensMap;
	}
}
