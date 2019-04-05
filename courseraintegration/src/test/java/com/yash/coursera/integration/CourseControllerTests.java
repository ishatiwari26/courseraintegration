package com.yash.coursera.integration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.components.CourseraComponent;
import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.controller.CourseController;
import com.yash.coursera.integration.helper.CommonUtils;
import com.yash.coursera.integration.helper.FileOpUtils;


@RunWith(SpringRunner.class)
@WebMvcTest(CourseController.class)
public class CourseControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FileOpUtils fileOpUtil;

	@MockBean
	CourseraComponent courseraComponent;

	@MockBean
	CommonUtils commonUtils;

	@MockBean
	RestTemplate restTemplate;

	@MockBean
	ResponseEntity<String> response;
	
	@MockBean
	private JobLauncher jobLauncher;

	@MockBean	
	BatchConfig config;

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void shouldGenerateToken() throws Exception {
		 mockMvc.perform(get("/generateToken")).andExpect(status().is(302));
	}

	@Test
	public void shouldCallBack() throws Exception {
		String code = "testCode";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("access_token", "testAccessToken");
		jsonObject.put("refresh_token", "testRefreshToken");
		when(courseraComponent.getAccessToken(Mockito.anyString(), Mockito.any(RestTemplate.class))).thenReturn(jsonObject);
		doNothing().when(commonUtils).writeToFile(Mockito.anyString(), Mockito.anyString());
		mockMvc.perform(get("/callback").param("code", code)).andExpect(status().isOk());
	}
	@Test
	public void shouldCallBackWhenCodeIsNull() throws Exception {
		String code = null;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("access_token", "testAccessToken");
		jsonObject.put("refresh_token", "testRefreshToken");
		when(courseraComponent.getAccessToken(Mockito.anyString(), Mockito.any(RestTemplate.class))).thenReturn(jsonObject);
		doNothing().when(commonUtils).writeToFile(Mockito.anyString(), Mockito.anyString());
		mockMvc.perform(get("/callback").param("code", code)).andExpect(status().isOk());
	}
	@Test
	public void shouldCallBackWhenAccessTokenIsNull() throws Exception {
		String code = "testCode";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("access_token", "");
		jsonObject.put("refresh_token", "testRefreshToken");
		when(courseraComponent.getAccessToken(Mockito.anyString(), Mockito.any(RestTemplate.class))).thenReturn(jsonObject);
		doNothing().when(commonUtils).writeToFile(Mockito.anyString(), Mockito.anyString());
		mockMvc.perform(get("/callback").param("code", code)).andExpect(status().isOk());
	}

	@Test
	public void shouldCallBackWhenRefreshTokenIsNull() throws Exception {
		String code = "testCode";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("access_token", "testAccessToken");
		jsonObject.put("refresh_token", "");
		when(courseraComponent.getAccessToken(Mockito.anyString(), Mockito.any(RestTemplate.class))).thenReturn(jsonObject);
		doNothing().when(commonUtils).writeToFile(Mockito.anyString(), Mockito.anyString());
		mockMvc.perform(get("/callback").param("code", code)).andExpect(status().isOk());
	}
	
	@Test
	public void shouldLoadContentAPI() throws Exception{
		Mockito.mock(JobParameter.class);		
		Mockito.mock(JobParameters.class);
		JobExecution jobExecution = new JobExecution(1L);
		jobExecution.setStatus(BatchStatus.COMPLETED);
		Mockito.mock(HashMap.class);
		Job job = new SimpleJob();
		Mockito.when(config.processJob()).thenReturn(job);
		
		Mockito.when(jobLauncher.run(Mockito.any(Job.class),Mockito.any(JobParameters.class))).thenReturn(jobExecution);
		
		mockMvc.perform(get("/loadContentAPI").contentType("application/json"))
		.andExpect(status().isOk());
		  assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	/*@Test(expected=JobExecutionAlreadyRunningException.class)
	public void shouldThrowExeption_WhenLoadContentAPI() throws Exception{
//		exception.expect(JobExecutionAlreadyRunningException.class);
		Mockito.mock(JobParameter.class);		
//		Mockito.mock(JobParameters.class);
		JobParameters jobParams=new JobParameters(Mockito.mock(HashMap.class));
		JobExecution jobExecution = new JobExecution(1L);
		jobExecution.setStatus(BatchStatus.FAILED);
		JobExecution jobExecution = Mockito.mock(JobExecution.class);
		Mockito.when(jobExecution.getStatus()).thenThrow(JobExecutionAlreadyRunningException.class);
		Mockito.mock(HashMap.class);
		Job job = new SimpleJob();
		Mockito.when(config.processJob()).thenReturn(job);
		JobParameters jobParameters = 
				  new JobParametersBuilder()
				  .addLong("time",System.currentTimeMillis()).toJobParameters();
		Mockito.when(jobLauncher.run(Mockito.any(Job.class),Mockito.any(JobParameters.class)))
		.thenThrow(new JobExecutionAlreadyRunningException("New Instance created"));
		
		Mockito.when(jobLauncher.run(Mockito.any(Job.class),Mockito.any(JobParameters.class))).thenReturn(jobExecution);
		
		mockMvc.perform(get("/loadContentAPI").contentType("application/json"));
//		.andExpect(status().isInternalServerError());
	}*/
	@Test
	public void shouldLoadProgramAPI() throws Exception{
		Mockito.mock(JobParameter.class);		
		Mockito.mock(JobParameters.class);
		JobExecution jobExecution = new JobExecution(1L);
		jobExecution.setStatus(BatchStatus.COMPLETED);
		Mockito.mock(HashMap.class);
		Job job = new SimpleJob();
		Mockito.when(config.processJob()).thenReturn(job);
		
		Mockito.when(jobLauncher.run(Mockito.any(Job.class),Mockito.any(JobParameters.class))).thenReturn(jobExecution);
		
		mockMvc.perform(get("/loadProgramAPI").contentType("application/json"))
		.andExpect(status().isOk());
		 assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	@Test
	public void shouldLoadStatusAPI() throws Exception{
		Mockito.mock(JobParameter.class);		
		Mockito.mock(JobParameters.class);
		JobExecution jobExecution = new JobExecution(1L);
		jobExecution.setStatus(BatchStatus.COMPLETED);
		Mockito.mock(HashMap.class);
		Job job = new SimpleJob();
		Mockito.when(config.processJob()).thenReturn(job);
		
		Mockito.when(jobLauncher.run(Mockito.any(Job.class),Mockito.any(JobParameters.class))).thenReturn(jobExecution);
		
		mockMvc.perform(get("/loadStatusAPI").contentType("application/json"))
		.andExpect(status().isOk());
		 assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	/*@Test
	public void shouldLoadUserInvitationAPI() throws Exception{
		Mockito.mock(JobParameter.class);		
		Mockito.mock(JobParameters.class);
		JobExecution jobExecution = new JobExecution(1L);
		jobExecution.setStatus(BatchStatus.COMPLETED);
		Mockito.mock(HashMap.class);
		Job job = new SimpleJob();
		Mockito.when(config.processInviteJob()).thenReturn(job);
		
		Mockito.when(jobLauncher.run(Mockito.any(Job.class),Mockito.any(JobParameters.class))).thenReturn(jobExecution);
		
		mockMvc.perform(get("/loadInvitation").contentType("application/json"))
		.andExpect(status().isOk());
		 assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}*/
	/*@Test
	public void shouldUnAutjorixedWhenPostUserWhoNotInvided() throws Exception{
		JSONObject userInviteJsonObject=new JSONObject();
		userInviteJsonObject.accumulate("externalId", "testExternalId");
		userInviteJsonObject.accumulate("fullName", "testFullName");
		userInviteJsonObject.accumulate("email", "testEmail");
		mockMvc.perform(get("/invitation?programId=Q0Wzd5osEei1PwqN7iH8Jg").contentType("application/json")
				.content("externalId=testExternalId,fullName=testFullName,email=testEmail")).andExpect(status().is(404));
	}*/
	/*private JSONObject getDumyJSONObject() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("name", "YASH Technologies Learning Program");
		jsonObj.put("tagline", "Start learning on Coursera!");

		JSONObject jsonObjChild = new JSONObject();
		jsonObjChild.put("contentId", "zj2VppjQEeWh0Q5bBaG7rw");
		jsonObjChild.put("contentType", "Specialization");

		JSONArray contentArray = new JSONArray();
		contentArray.put(jsonObjChild);

		jsonObj.put("contentIds", contentArray);

		jsonObj.put("id", "Q0Wzd5osEei1PwqN7iH8Jg");
		jsonObj.put("url", "https://www.coursera.org/programs/yash-technologies-learning-program-ziplt");

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(jsonObj);

		JSONObject mainObj = new JSONObject();
		mainObj.put("elements", jsonArray);
		return mainObj;
	}*/
}