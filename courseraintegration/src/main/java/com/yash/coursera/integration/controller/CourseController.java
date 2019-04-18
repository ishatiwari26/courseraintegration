package com.yash.coursera.integration.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.jcraft.jsch.JSch;
import com.yash.coursera.integration.components.CourseraTokenComponent;
import com.yash.coursera.integration.components.SFTPComponent;
import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.helper.GlobalConstants;

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

	@Value("${STATUS_OUTPUT_FILE}")
	private String statusFileName;

	@Value("${GET_PROGRAM_API}")
	private String getProgramListApi;

	@Value("${GET_CONTENTS_API}")
	private String getContentsApi;

	@Value("${GET_STATUS_API}")
	private String getStatusApi;

	@Value("${INVITATION_OUTPUT_FILE}")
	private String invitationFileName;

	@Value("${GET_INVITATION_API}")
	private String getInvitationApi;

	@Value("${SFTPHOST}")
	private String getSFTPHost;

	@Value("${SFTPUSER}")
	private String getSFTPUser;

	@Value("${SFTPPASS}")
	private String getSFTPPassword;

	@Value("${SFTPINBOUNDDIR}")
	private String getSFTPInboundDirectory;

	@Value("${SFTPPROCESSDIR}")
	private String getSFTPProcessDirectory;

	@Value("${SFTPBACKUPDIR}")
	private String getSFTPBackupDirectory;

	@Value("${SFTPEXCEPTIONDIR}")
	private String getSFTPExceptionDirectory;

	@Value("${LOCALPATH}")
	private String getLocalPath;

	@Value("${FILE_NAME}")
	private String getFileName;

	String accessToken, refreshToken;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	BatchConfig batchConfig;

	@Autowired
	CourseraTokenComponent courseraTokenComponent;

	@Autowired
	FileOpUtils commonUtils;

	@Autowired
	private SFTPComponent sftpComponent;

	private static final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);

	@ApiIgnore
	@RequestMapping(value = "/callback", method = RequestMethod.GET)
	public String callback(@RequestParam(required = false, name = GlobalConstants.CODE_KEY) String code,
			HttpServletRequest req, HttpServletResponse res) {
		RestTemplate restTemplate = new RestTemplate();

		if (code != null) {
			JSONObject JsonObject = courseraTokenComponent.getAccessToken(code, restTemplate);
			accessToken = (String) JsonObject.get(GlobalConstants.ACCESS_TOKEN_KEY);
			refreshToken = (String) JsonObject.get(GlobalConstants.REFRESH_TOKEN_KEY);
			if (accessToken != "" && refreshToken != "")
				commonUtils.writeToFile(new String[] { GlobalConstants.ACCESS_TOKEN_KEY + "=" + accessToken,
						GlobalConstants.REFRESH_TOKEN_KEY + "=" + refreshToken });
		}
		LOGGER.trace("callback needed more information - {}", refreshToken);
		LOGGER.debug("callback needed to debug - {}", refreshToken);
		LOGGER.info("callback took input - {}", refreshToken);
		LOGGER.warn("callback needed to warn - {}", refreshToken);
		LOGGER.error("callback encountered an error with value - {}", refreshToken);
		return "Tokens Generated: <br>AccessToken: " + accessToken + "<br> " + " RefreshToken: " + refreshToken;
	}

	@ApiIgnore
	@RequestMapping(value = "/generateToken", method = RequestMethod.GET)
	public void generateToken(HttpServletRequest req, HttpServletResponse res) throws Exception {
		res.sendRedirect(getCodeUri + clientId);

	}

	@GetMapping(value = "/accessSFTPFile")
	public String sftpFileTransfer() {
		boolean SFTPStatus = false;
		Integer statusCount = null;
		String response = "";
		sftpComponent.setJsch(new JSch());
		statusCount = sftpComponent.downloadFileRemoteToLocal(getSFTPInboundDirectory.concat(getFileName),getLocalPath);
		if (statusCount != null)
			SFTPStatus = sftpComponent.uploadFileLocalToRemote(getLocalPath.concat(getFileName),getSFTPProcessDirectory);
		if (SFTPStatus)
			statusCount = sftpComponent.downloadFileRemoteToLocal(getSFTPProcessDirectory.concat(getFileName),getLocalPath);
		if (SFTPStatus && statusCount != null)
			response = "Successfully moved file from Process To Local!!";
		else
			response = "Fail to move file!!";
		return response;
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
			ex = jobLauncher.run(batchConfig.processJob(), jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			LOGGER.error(e.getMessage());
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
			ex = jobLauncher.run(batchConfig.processJob(), jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return ex.getStatus();
	}

	@GetMapping(value = "/loadStatusAPI")
	public BatchStatus loadStatusAPI() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		JobExecution ex = null;

		Map<String, JobParameter> confMap = new HashMap<String, JobParameter>();
		confMap.put("time", new JobParameter(System.currentTimeMillis()));
		confMap.put("jobName", new JobParameter("loadStatusAPI"));
		confMap.put("fileName", new JobParameter(statusFileName));
		confMap.put("apiUrl", new JobParameter(getStatusApi));
		JobParameters jobParameters = new JobParameters(confMap);
		try {
			ex = jobLauncher.run(batchConfig.processJob(), jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return ex.getStatus();
	}

	@GetMapping(value = "/loadInvitationAPI")
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
			ex = jobLauncher.run(batchConfig.processInviteJob(), jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return ex.getStatus();
	}

	/*
	 * @ApiOperation(value = "send invite to users", response = List.class)
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 201, message =
	 * "Successfully send invite"),
	 * 
	 * @ApiResponse(code = 401, message =
	 * "You are not authorized to view the resource"),
	 * 
	 * @ApiResponse(code = 403, message =
	 * "Accessing the resource you were trying to reach is forbidden"),
	 * 
	 * @ApiResponse(code = 404, message =
	 * "The resource you were trying to reach is not found") })
	 * 
	 * @RequestMapping(value = "/invitation/{programId}", method =
	 * RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes
	 * = MediaType.APPLICATION_JSON_VALUE)
	 * 
	 * @ResponseBody public ResponseEntity<String>
	 * getInviteList(@PathVariable("programId") String programId,
	 * 
	 * @RequestBody User userInvitation, HttpServletRequest req,
	 * HttpServletResponse res) { ResponseEntity<String> response = null;
	 * Map<String, String> tokensMap = FileOpUtils.readAccessToken(); try {
	 * 
	 * if (tokensMap.get("access_token") == null ||
	 * tokensMap.get("access_token") == "") { response = new ResponseEntity<>
	 * ("Authorize client and generate token by calling /generateToken API",
	 * HttpStatus.UNAUTHORIZED); } else { response =
	 * courseraComponent.postInvitation(programId,
	 * tokensMap.get("access_token"), userInvitation); }
	 * 
	 * } catch (RestClientException e) { try { accessToken =
	 * courseraComponent.getNewAccessToken(tokensMap.get("refresh_token"));
	 * response = courseraComponent.postInvitation(programId, accessToken,
	 * userInvitation); } catch (RestClientException ex) { response = new
	 * ResponseEntity<>
	 * ("Authorize client  and generate token by calling /generateToken API",
	 * HttpStatus.UNAUTHORIZED); } } return response; }
	 */

}
