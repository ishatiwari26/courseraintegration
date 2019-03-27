package com.yash.coursera.integration;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.controller.CourseController;
import com.yash.coursera.integration.helper.CommonUtils;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.service.CourseraService;


@RunWith(SpringRunner.class)
@WebMvcTest(CourseController.class)
public class CourseControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FileOpUtils fileOpUtil;

	@MockBean
	CourseraService courseraService;

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

	@Test
	public void shouldGenerateToken() throws Exception {
		 mockMvc.perform(get("/generateToken")).andExpect(status().is(302));
	}

	@Test
	public void shouldCallBackWhenCodeIsNotNull() throws Exception {
		String code = "testCode";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("access_token", "testAccessToken");
		jsonObject.put("refresh_token", "testRefreshToken");
		when(courseraService.getAccessToken(Mockito.anyString(), Mockito.any(RestTemplate.class))).thenReturn(jsonObject);
		doNothing().when(commonUtils).writeToFile(Mockito.anyString(), Mockito.anyString());
		mockMvc.perform(get("/callback").param("code", code)).andExpect(status().isOk());
	}
	@Test
	public void shouldCallBackWhenCodeIsNull() throws Exception {
		String code = null;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("access_token", "testAccessToken");
		jsonObject.put("refresh_token", "testRefreshToken");
		when(courseraService.getAccessToken(Mockito.anyString(), Mockito.any(RestTemplate.class))).thenReturn(jsonObject);
		doNothing().when(commonUtils).writeToFile(Mockito.anyString(), Mockito.anyString());
		mockMvc.perform(get("/callback").param("code", code)).andExpect(status().isOk());
	}
	@Test
	public void shouldCallBackWhenAccessTokenIsNull() throws Exception {
		String code = "testCode";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("access_token", "");
		jsonObject.put("refresh_token", "testRefreshToken");
		when(courseraService.getAccessToken(Mockito.anyString(), Mockito.any(RestTemplate.class))).thenReturn(jsonObject);
		doNothing().when(commonUtils).writeToFile(Mockito.anyString(), Mockito.anyString());
		mockMvc.perform(get("/callback").param("code", code)).andExpect(status().isOk());
	}

	@Test
	public void shouldCallBackWhenRefreshTokenIsNull() throws Exception {
		String code = "testCode";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("access_token", "testAccessToken");
		jsonObject.put("refresh_token", "");
		when(courseraService.getAccessToken(Mockito.anyString(), Mockito.any(RestTemplate.class))).thenReturn(jsonObject);
		doNothing().when(commonUtils).writeToFile(Mockito.anyString(), Mockito.anyString());
		mockMvc.perform(get("/callback").param("code", code)).andExpect(status().isOk());
	}
	public void shouldReturnProgramList() throws Exception {
		JSONObject dumyJSON = getDumyJSONObject();
		ResponseEntity<String> response = new ResponseEntity(dumyJSON, HttpStatus.OK);
		when(courseraService.callProgramsAPI(Mockito.anyInt(), Mockito.anyInt(),Mockito.anyString())).thenReturn(response);
		mockMvc.perform(get("/getProgramList").contentType("application/json").param("start", "0").param("limit", "10"))
				.andExpect(status().isOk());
	}
	/*@Test
	public void shouldRegenerateTokenWhileGotExceptionInProgramList() throws Exception {
		when(courseraService.callProgramsAPI(Mockito.anyInt(), Mockito.anyInt(),Mockito.anyString())).thenThrow(RestClientException.class);
		mockMvc.perform(get("/getProgramList").contentType("application/json").param("start", "0").param("limit", "10"))
				.andExpect(status().is(401));
	}*/
	@Test
	public void shouldReturnContentList() throws Exception {
		JSONObject dumyJSON = getDumyJSONObject();
		ResponseEntity<String> response = new ResponseEntity(dumyJSON, HttpStatus.OK);
		when(courseraService.callContentsAPI(Mockito.anyInt(), Mockito.anyInt(),Mockito.anyString())).thenReturn(response);
		mockMvc.perform(get("/getContentsList").contentType("application/json").param("start", "0").param("limit", "10"))
				.andExpect(status().isOk());
	}
	/*@Test
	public void shouldRegenerateTokenWhileGotExceptionInContentList() throws Exception {
		when(courseraService.callContentsAPI(Mockito.anyInt(), Mockito.anyInt(),Mockito.anyString())).thenThrow(RestClientException.class);
		mockMvc.perform(get("/getContentsList").contentType("application/json").param("start", "0").param("limit", "10"))
				.andExpect(status().is(401));
	}*/
	private JSONObject getDumyJSONObject() {
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
	}
}