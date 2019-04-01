package com.yash.coursera.integration.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.helper.CommonUtils;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.model.User;
import com.yash.coursera.integration.service.CourseraService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class CourseController {

	@Value("${CLIENT_ID}")
	private String clientId;

	@Value("${GET_CODE_URI}")
	private String getCodeUri;

	@Value("${PROGRAM_OUTPUT_FILE}")
	private String programFileName;

	@Value("${CONTENT_OUTPUT_FILE}")
	private String contentFileName;

	@Value("${GET_PROGRAM_API}")
	private String getProgramListApi;

	@Value("${GET_CONTENTS_API}")
	private String getContentsApi;

	@Value("${INVITATION_OUTPUT_FILE}")
	private String invitationFileName;

	@Value("${GET_INVITATION_API}")
	private String getInvitationApi;

	String accessToken, refreshToken;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	BatchConfig config;

	@Autowired
	CourseraService courseraService = new CourseraService();

	CommonUtils commonUtils = new CommonUtils();

	@ApiIgnore
	@RequestMapping(value = "/callback", method = RequestMethod.GET)
	public String callback(@RequestParam(required = false, name = GlobalConstants.CODE_KEY) String code,
			HttpServletRequest req, HttpServletResponse res) {
		RestTemplate restTemplate = new RestTemplate();

		if (code != null) {
			JSONObject JsonObject = courseraService.getAccessToken(code, restTemplate);
			accessToken = (String) JsonObject.get(GlobalConstants.ACCESS_TOKEN_KEY);
			refreshToken = (String) JsonObject.get(GlobalConstants.REFRESH_TOKEN_KEY);
			if (accessToken != "" && refreshToken != "")
				commonUtils.writeToFile(accessToken, refreshToken);
		}
		return "Tokens Generated: <br>AccessToken: " + accessToken + "<br> " + " RefreshToken: " + refreshToken;
	}

	@ApiOperation(value = "Get list of programs", response = List.class)

	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list of programs"),

			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),

			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),

			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@RequestMapping(value = "/getProgramList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> getProgramList(
			@RequestParam(required = false, name = GlobalConstants.START) Integer start,
			@RequestParam(required = false, name = GlobalConstants.LIMIT) Integer limit, HttpServletRequest req,
			HttpServletResponse res) {
		ResponseEntity<String> response = null;
		start = start != null ? start : 0;
		limit = limit != null && limit > 0 ? limit : 100;
		Map<String, String> tokensMap = FileOpUtils.readAccessToken();
		try {
			if (tokensMap.get("access_token") == null || tokensMap.get("access_token") == "") {
				response = new ResponseEntity<>("Authorize client and generate token by calling /generateToken API",
						HttpStatus.UNAUTHORIZED);
			} else {
				response = courseraService.callProgramsAPI(start, limit, tokensMap.get("access_token"));
			}
		} catch (RestClientException e) {
			try {
				accessToken = courseraService.getNewAccessToken(tokensMap.get("refresh_token"));
				response = courseraService.callProgramsAPI(start, limit, accessToken);
			} catch (RestClientException ex) {
				response = new ResponseEntity<>("Authorize client  and generate token by calling /generateToken API",
						HttpStatus.UNAUTHORIZED);
			}
		}
		return response;
	}

	@ApiOperation(value = "Get list of content in programs", response = List.class)

	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list of content in programs"),

			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),

			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),

			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@RequestMapping(value = "/getContentsList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> getContentsList(
			@RequestParam(required = false, name = GlobalConstants.START) Integer start,
			@RequestParam(required = false, name = GlobalConstants.LIMIT) Integer limit, HttpServletRequest req,
			HttpServletResponse res) {
		ResponseEntity<String> response = null;
		start = start != null ? start : 0;
		limit = limit != null && limit > 0 ? limit : 100;
		Map<String, String> tokensMap = FileOpUtils.readAccessToken();
		try {
			if (tokensMap.get("access_token") == null || tokensMap.get("access_token") == "") {
				response = new ResponseEntity<>("Authorize client and generate token by calling /generateToken API",
						HttpStatus.UNAUTHORIZED);
			} else {
				response = courseraService.callContentsAPI(start, limit, tokensMap.get("access_token"));
			}

		} catch (RestClientException e) {
			try {
				accessToken = courseraService.getNewAccessToken(tokensMap.get("refresh_token"));
				response = courseraService.callContentsAPI(start, limit, accessToken);
			} catch (RestClientException ex) {
				response = new ResponseEntity<>("Authorize client  and generate token by calling /generateToken API",
						HttpStatus.UNAUTHORIZED);
			}
		}
		return response;
	}

	@ApiIgnore
	@RequestMapping(value = "/generateToken", method = RequestMethod.GET)
	public void generateToken(HttpServletRequest req, HttpServletResponse res) throws Exception {
		res.sendRedirect(getCodeUri + clientId);

	}

	@GetMapping(value = "/loadContentAPI")
	public BatchStatus loadContentAPI() throws JobExecutionAlreadyRunningException, JobRestartException,
	JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		JobExecution ex = null;

		Map<String, JobParameter> confMap = new HashMap<String, JobParameter>();
		confMap.put("time", new JobParameter(System.currentTimeMillis()));
		confMap.put("jobName", new JobParameter("loadContentAPI"));
		confMap.put("fileName", new JobParameter(contentFileName));
		confMap.put("apiUrl", new JobParameter(getContentsApi));
		JobParameters jobParameters = new JobParameters(confMap);
		try {
			ex = jobLauncher.run(config.processJob(), jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}
		return ex.getStatus();
	}

	@GetMapping(value = "/loadProgramAPI")
	public BatchStatus loadProgramAPI() throws JobExecutionAlreadyRunningException, JobRestartException,
	JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		JobExecution ex = null;

		Map<String, JobParameter> confMap = new HashMap<String, JobParameter>();
		confMap.put("time", new JobParameter(System.currentTimeMillis()));
		confMap.put("jobName", new JobParameter("loadProgramAPI"));
		confMap.put("fileName", new JobParameter(programFileName));
		confMap.put("apiUrl", new JobParameter(getProgramListApi));
		JobParameters jobParameters = new JobParameters(confMap);
		try {
			ex = jobLauncher.run(config.processJob(), jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}
		return ex.getStatus();
	}

	@GetMapping(value = "/loadInvitation")
	public BatchStatus loadInvitationAPI() throws JobExecutionAlreadyRunningException, JobRestartException,
	JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		JobExecution ex = null;

		Map<String, JobParameter> confMap = new HashMap<String, JobParameter>();
		confMap.put("time", new JobParameter(System.currentTimeMillis()));
		confMap.put("jobName", new JobParameter("loadInvitationAPI"));
		confMap.put("fileName", new JobParameter(invitationFileName));
		confMap.put("apiUrl", new JobParameter(getInvitationApi));
		JobParameters jobParameters = new JobParameters(confMap);

		try {
			ex = jobLauncher.run(config.processInviteJob(), jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}
		return ex.getStatus();
	}

	@ApiOperation(value = "send invite to users", response = List.class)

	@ApiResponses(value = { @ApiResponse(code = 201, message = "Successfully send invite"),

			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),

			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),

			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@RequestMapping(value = "/invitation/{programId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> getInviteList(@PathVariable("programId") String programId,
			@RequestBody User userInvitation, HttpServletRequest req, HttpServletResponse res) {
		ResponseEntity<String> response = null;
		Map<String, String> tokensMap = FileOpUtils.readAccessToken();
		try {

			if (tokensMap.get("access_token") == null || tokensMap.get("access_token") == "") {
				response = new ResponseEntity<>("Authorize client and generate token by calling /generateToken API",
						HttpStatus.UNAUTHORIZED);
			} else {
				response = courseraService.postInvitation(programId, tokensMap.get("access_token"), userInvitation);
			}

		} catch (RestClientException e) {
			try {
				accessToken = courseraService.getNewAccessToken(tokensMap.get("refresh_token"));
				response = courseraService.postInvitation(programId, accessToken, userInvitation);
			} catch (RestClientException ex) {
				response = new ResponseEntity<>("Authorize client  and generate token by calling /generateToken API",
						HttpStatus.UNAUTHORIZED);
			}
		}
		return response;
	}

}
