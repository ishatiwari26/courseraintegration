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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

	@Value("${client.id}")
	private String clientId;

	@Value("${auth.code.uri}")
	private String getCodeUri;

	@Value("${program.output.file}")
	private String programFileName;

	@Value("${content.output.file}")
	private String contentFileName;

	@Value("${status.output.file}")
	private String statusFileName;

	@Value("${program.api}")
	private String getProgramListApi;

	@Value("${content.api}")
	private String getContentsApi;

	@Value("${status.api}")
	private String getStatusApi;

	@Value("${invitation.output.file}")
	private String invitationFileName;

	@Value("${invitation.api}")
	private String getInvitationApi;

	@Value("${sftp.host}")
	private String getSFTPHost;

	@Value("${sftp.user}")
	private String getSFTPUser;

	@Value("${sftp.password}")
	private String getSFTPPassword;

	@Value("${sftp.inbound.dir}")
	private String getSFTPInboundDirectory;

	@Value("${sftp.process.dir}")
	private String getSFTPProcessDirectory;

	@Value("${sftp.backup.dir}")
	private String getSFTPBackupDirectory;

	@Value("${sftp.exception.dir}")
	private String getSFTPExceptionDirectory;

	@Value("${local.user.file.dir.path}")
	private String getLocalPath;

	@Value("${user.file.name}")
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
		return "Tokens Generated: <br>AccessToken: " + accessToken + "<br> " + " RefreshToken: " + refreshToken;
	}

	@ApiIgnore
	@RequestMapping(value = "/generateToken", method = RequestMethod.GET)
	public void generateToken(HttpServletRequest req, HttpServletResponse res) throws Exception {
		res.sendRedirect(getCodeUri + clientId);

	}


	@GetMapping(value = "/loadContentAPI")
	public ResponseEntity<String> loadContentAPI() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		ResponseEntity<String> response = null;
		JobExecution ex = null;

		if (isNotAuthorized()) {
			return new ResponseEntity("Authorize client  and generate token by calling /generateToken API",
					HttpStatus.UNAUTHORIZED);
		}

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
			LOGGER.error("Exception while loading content API [loadContentAPI] :: " + e.getMessage());
			e.printStackTrace();
		}
		if ((ex.getStatus()).equals(BatchStatus.COMPLETED)) {
			response = new ResponseEntity("Job Executed Successfully", HttpStatus.OK);
		} else {
			response = new ResponseEntity("Job Failed", HttpStatus.SEE_OTHER);
		}
		return response;
	}

	@GetMapping(value = "/loadProgramAPI")
	public ResponseEntity<String> loadProgramAPI() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		ResponseEntity<String> response = null;
		JobExecution ex = null;

		if (isNotAuthorized()) {
			return new ResponseEntity("Authorize client  and generate token by calling /generateToken API",
					HttpStatus.UNAUTHORIZED);

		}

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
			LOGGER.error("Exception while loading program API [loadProgramAPI] :: " + e.getMessage());
			e.printStackTrace();
		}
		if ((ex.getStatus()).equals(BatchStatus.COMPLETED)) {
			response = new ResponseEntity("Job Executed Successfully", HttpStatus.OK);
		} else {
			response = new ResponseEntity("Job Failed", HttpStatus.SEE_OTHER);
		}
		return response;
	}

	@GetMapping(value = "/loadStatusAPI")
	public ResponseEntity<String> loadStatusAPI() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		ResponseEntity<String> response = null;
		JobExecution ex = null;

		if (isNotAuthorized()) {
			return new ResponseEntity("Authorize client  and generate token by calling /generateToken API",
					HttpStatus.UNAUTHORIZED);

		}
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
			LOGGER.error("Exception while loading status API [loadStatusAPI] :: " + e.getMessage());
			e.printStackTrace();
		}
		if ((ex.getStatus()).equals(BatchStatus.COMPLETED)) {
			response = new ResponseEntity("Job Executed Successfully", HttpStatus.OK);
		} else {
			response = new ResponseEntity("Job Failed", HttpStatus.SEE_OTHER);
		}
		return response;
	}

	@GetMapping(value = "/loadInvitationAPI")
	public ResponseEntity<String> loadInvitationAPI() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		ResponseEntity<String> response = null;
		JobExecution ex = null;
		boolean SFTPStatus = false;
		sftpComponent.setJsch(new JSch());
		SFTPStatus = sftpComponent.moveInboundToLocalViaProcess(getSFTPInboundDirectory, getSFTPProcessDirectory,
				getLocalPath, getFileName, false);
		if (SFTPStatus) {
			if (isNotAuthorized()) {
				return new ResponseEntity("Authorize client  and generate token by calling /generateToken API",
						HttpStatus.UNAUTHORIZED);
			}
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
				LOGGER.error("Exception while loading invitation API [loadInvitationAPI] :: " + e.getMessage());
				e.printStackTrace();
			}

			if ((ex.getStatus()).equals(BatchStatus.COMPLETED)) {
				SFTPStatus = sftpComponent.uploadFileLocalToRemote(getLocalPath.concat(getFileName),
						getSFTPBackupDirectory, true);
				response = new ResponseEntity("Job Executed Successfully", HttpStatus.OK);
			} else {
				SFTPStatus = sftpComponent.uploadFileLocalToRemote(getLocalPath.concat(getFileName),
						getSFTPExceptionDirectory,true);
				response = new ResponseEntity("Job Failed", HttpStatus.SEE_OTHER);
			}
		} else {
			response = new ResponseEntity("Fail to move file!!", HttpStatus.NOT_FOUND);
		}
		return response;
	}

	private boolean isNotAuthorized() {
		Map<String, String> tokensMap = commonUtils.readAccessToken();
		accessToken = tokensMap.get(GlobalConstants.ACCESS_TOKEN_KEY);
		refreshToken = tokensMap.get(GlobalConstants.REFRESH_TOKEN_KEY);
		return (accessToken == null && refreshToken == null);
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
