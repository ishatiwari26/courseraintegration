package com.yash.coursera.integration.components;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.helper.GlobalConstants;
import com.yash.coursera.integration.helper.LMSAuthorizationHelper;
import com.yash.coursera.integration.model.SFLmsMapper;

@Component
public class LMSPostStatusComponent {

	@Value("${lms.post.status.api}")
	private String lmsPostStatusApiUrl;

	@Autowired
	private LMSAuthorizationHelper lmsAuthorizationHelper;

	private String lmsAuthToken;

	RestTemplate restTemplate = new RestTemplate();

	private final Logger logger = LoggerFactory.getLogger(LMSPostStatusComponent.class);

	public void postLMSCoursesStatus(List<SFLmsMapper> mappers) {

		if (!lmsAuthorizationHelper.isLMSAuthTokenValid()) {
			lmsAuthToken = lmsAuthorizationHelper.getLMSAuthToken();
			// lmsAuthToken =
			// "eyJzaWduYXR1cmUiOiJLU3k4aVZhRFlaZG1RUlMwaittTWFkbHErMUJIV0tHYkNqdEFndGRrWjg3eVlBM251Q2Fna3pENVRVaDJjTk03Z25TbU5NZWx1VG9odzNQV1FlTm91ZTdZS1JTOVBUeGdlcTh5SHcwaFhlZm9HQTNlN05tdklZS2pWSFBrZWpHU2hGUXFJdGNTTmhIMXZ2dUZDaHVCc1dSK0dHclN0V1N3WGFoN0ZYUzhzN2s9IiwidG9rZW5Db250ZW50Ijoie1wicGVyc29uR3VpZFwiOlwiRjQ3MjQxQzczOEE4NEY4M0I0Njg1MkI5MjU3QUE2NjdcIixcInVzZXJJZFwiOlwiY291cnNlcmFfaW50ZWdcIixcImNvbXBhbnlJZFwiOlwicGFydGxtczAwMzdcIixcImNsaWVudElkXCI6XCJwYXJ0bG1zMDAzN1wiLFwiaXNzdWVkQXRcIjoxNTU3NzMxODMwODQ4LFwiZXhwaXJlc0luXCI6MTgwMCxcImlzc3VlZEZvclwiOlwibGVhcm5pbmdfcHVibGljX2FwaVwiLFwidXNlclR5cGVcIjpcImFkbWluXCJ9In0=";
		}
		JSONArray lmsCourseMapperArray = new JSONArray();
		JSONObject lmsCoursesPayload = new JSONObject();
		mappers.forEach(mapper -> lmsCourseMapperArray.put(getLMSCourseStatusPayload(mapper)));

		lmsCoursesPayload.put(GlobalConstants.STATUS_COURSE_STATUS_OBJECT, lmsCourseMapperArray);

		HttpHeaders headers = new HttpHeaders();
		headers.set(GlobalConstants.AUTHORIZATION_KEY, "Bearer " + lmsAuthToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<?> entity = new HttpEntity<Object>(lmsCoursesPayload, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(lmsPostStatusApiUrl, HttpMethod.POST, entity,
					String.class);
			logger.info("response status : " + response.getStatusCode());
			logger.info("response status : " + response.getBody());
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
		}
	}

	private JSONObject getLMSCourseStatusPayload(SFLmsMapper mapper) {
		JSONObject lmsMapper = new JSONObject();
		String[] strId = mapper.getId().equals("") ? null : mapper.getId().split("~");
		if (strId.length > 0) {
			lmsMapper.put(GlobalConstants.STATUS_USER_ID_KEY, strId[0]);
			lmsMapper.put(GlobalConstants.COURSE_ID, strId[2]);
		}
		lmsMapper.put(GlobalConstants.PROVIDER_ID, mapper.getProviderID());
		lmsMapper.put(GlobalConstants.STATUS_COURSE_COMPLETED, mapper.getIsCompleted());
		if (mapper.getIsCompleted()) {
			lmsMapper.put(GlobalConstants.STATUS_COMPLETED_DATE, mapper.getCompletedAt());
			lmsMapper.put(GlobalConstants.STATUS_GRADE, mapper.getGrade());
		}

		return lmsMapper;
	}
}
