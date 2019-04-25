package com.yash.coursera.integration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
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
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.jcraft.jsch.JSch;
import com.yash.coursera.integration.components.CourseraTokenComponent;
import com.yash.coursera.integration.components.SFTPComponent;
import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.controller.CourseController;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;

@RunWith(SpringRunner.class)
@WebMvcTest(CourseController.class)
public class CourseControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FileOpUtils fileOpUtil;

	@MockBean
	CourseraTokenComponent courseraTokenComponent;

	@MockBean
	RestTemplate restTemplate;

	@MockBean
	ResponseEntity<String> response;

	@MockBean
	private JobLauncher jobLauncher;

	@MockBean
	BatchConfig config;

	@MockBean
	private SFTPComponent sftpComponent;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		JSch jsch = mock(JSch.class);
		sftpComponent.setJsch(jsch);
	}

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
		when(courseraTokenComponent.getAccessToken(Mockito.anyString(), Mockito.any(RestTemplate.class)))
				.thenReturn(jsonObject);
		doNothing().when(fileOpUtil).writeToFile(Mockito.anyObject());
		mockMvc.perform(get("/callback").param("code", code)).andExpect(status().isOk());
	}

	@Test
	public void shouldCallBackWhenCodeIsNull() throws Exception {
		String code = null;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("access_token", "testAccessToken");
		jsonObject.put("refresh_token", "testRefreshToken");
		when(courseraTokenComponent.getAccessToken(Mockito.anyString(), Mockito.any(RestTemplate.class)))
				.thenReturn(jsonObject);
		doNothing().when(fileOpUtil).writeToFile(Mockito.anyObject());
		mockMvc.perform(get("/callback").param("code", code)).andExpect(status().isOk());
	}

	@Test
	public void shouldCallBackWhenAccessTokenIsNull() throws Exception {
		String code = "testCode";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("access_token", "");
		jsonObject.put("refresh_token", "testRefreshToken");
		when(courseraTokenComponent.getAccessToken(Mockito.anyString(), Mockito.any(RestTemplate.class)))
				.thenReturn(jsonObject);
		doNothing().when(fileOpUtil).writeToFile(Mockito.anyObject());
		mockMvc.perform(get("/callback").param("code", code)).andExpect(status().isOk());
	}

	@Test
	public void shouldCallBackWhenRefreshTokenIsNull() throws Exception {
		String code = "testCode";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("access_token", "testAccessToken");
		jsonObject.put("refresh_token", "");
		when(courseraTokenComponent.getAccessToken(Mockito.anyString(), Mockito.any(RestTemplate.class)))
				.thenReturn(jsonObject);
		doNothing().when(fileOpUtil).writeToFile(Mockito.anyObject());
		mockMvc.perform(get("/callback").param("code", code)).andExpect(status().isOk());
	}

	@Test
	public void shouldLoadContentAPI() throws Exception {
		Mockito.mock(JobParameter.class);
		Mockito.mock(JobParameters.class);
		JobExecution jobExecution = new JobExecution(1L);
		jobExecution.setStatus(BatchStatus.COMPLETED);
		Mockito.mock(HashMap.class);
		Job job = new SimpleJob();
		when(config.processJob()).thenReturn(job);
		Map map = new HashMap<String, String>();
		map.put(GlobalConstants.ACCESS_TOKEN_KEY, "test");
		map.put(GlobalConstants.REFRESH_TOKEN_KEY, "test");
		when(fileOpUtil.readAccessToken()).thenReturn(map);
		when(jobLauncher.run(Mockito.any(Job.class), Mockito.any(JobParameters.class))).thenReturn(jobExecution);

		mockMvc.perform(get("/loadContentAPI").contentType("application/json")).andExpect(status().isOk());
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}

	@Test
	public void shouldLoadProgramAPI() throws Exception {
		Mockito.mock(JobParameter.class);
		Mockito.mock(JobParameters.class);
		JobExecution jobExecution = new JobExecution(1L);
		jobExecution.setStatus(BatchStatus.COMPLETED);
		Mockito.mock(HashMap.class);
		Job job = new SimpleJob();
		when(config.processJob()).thenReturn(job);
		Map map = new HashMap<String, String>();
		map.put(GlobalConstants.ACCESS_TOKEN_KEY, "test");
		map.put(GlobalConstants.REFRESH_TOKEN_KEY, "test");
		when(fileOpUtil.readAccessToken()).thenReturn(map);
		when(jobLauncher.run(Mockito.any(Job.class), Mockito.any(JobParameters.class))).thenReturn(jobExecution);

		mockMvc.perform(get("/loadProgramAPI").contentType("application/json")).andExpect(status().isOk());
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}

	@Test
	public void shouldLoadStatusAPI() throws Exception {
		Mockito.mock(JobParameter.class);
		Mockito.mock(JobParameters.class);
		JobExecution jobExecution = new JobExecution(1L);
		jobExecution.setStatus(BatchStatus.COMPLETED);
		Mockito.mock(HashMap.class);
		Job job = new SimpleJob();
		when(config.processJob()).thenReturn(job);
		Map map = new HashMap<String, String>();
		map.put(GlobalConstants.ACCESS_TOKEN_KEY, "test");
		map.put(GlobalConstants.REFRESH_TOKEN_KEY, "test");
		when(fileOpUtil.readAccessToken()).thenReturn(map);
		when(jobLauncher.run(Mockito.any(Job.class), Mockito.any(JobParameters.class))).thenReturn(jobExecution);

		mockMvc.perform(get("/loadStatusAPI").contentType("application/json")).andExpect(status().isOk());
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}

	@Test
	public void shouldLoadUserInvitationAPI() throws Exception {
		when(sftpComponent.moveInboundToLocalViaProcess(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(),Mockito.anyBoolean())).thenReturn(true);
		Mockito.mock(JobParameter.class);
		Mockito.mock(JobParameters.class);
		JobExecution jobExecution = new JobExecution(1L);
		jobExecution.setStatus(BatchStatus.COMPLETED);
		Mockito.mock(HashMap.class);
		Job job = new SimpleJob();
		Mockito.when(config.processInviteJob()).thenReturn(job);
		Map map = new HashMap<String, String>();
		map.put(GlobalConstants.ACCESS_TOKEN_KEY, "test");
		map.put(GlobalConstants.REFRESH_TOKEN_KEY, "test");
		when(fileOpUtil.readAccessToken()).thenReturn(map);
		Mockito.when(jobLauncher.run(Mockito.any(Job.class), Mockito.any(JobParameters.class)))
				.thenReturn(jobExecution);
		when(sftpComponent.uploadFileLocalToRemote(Mockito.anyString(),Mockito.anyString(),Mockito.anyBoolean())).thenReturn(true);
		mockMvc.perform(get("/loadInvitationAPI").contentType("application/json")).andExpect(status().isOk());
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	
	@Test
	public void shouldLoadUserInvitationAPI_WhenSFTPFalse() throws Exception {
		when(sftpComponent.moveInboundToLocalViaProcess(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(),Mockito.anyBoolean())).thenReturn(false);
		JobExecution jobExecution = new JobExecution(1L);
		when(sftpComponent.uploadFileLocalToRemote(Mockito.anyString(),Mockito.anyString(),Mockito.anyBoolean())).thenReturn(true);
		mockMvc.perform(get("/loadInvitationAPI").contentType("application/json")).andExpect(status().is(404));
		assertEquals(BatchStatus.STARTING, jobExecution.getStatus());
	}
}