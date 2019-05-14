package com.yash.coursera.integration.helper;

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

import com.yash.coursera.integration.model.SFLmsMapper;

@Component
public class LmsCourseHelper {

	@Value("${lms.post.courses.api}")
	private String lmsPostCoursesApiUrl;

	@Autowired
	LMSAuthorizationHelper lmsAuthorizationHelper;

	private String lmsAuthToken;

	RestTemplate restTemplate = new RestTemplate();

	private final Logger logger = LoggerFactory.getLogger(LmsCourseHelper.class);

	public void postLMSCourses(List<SFLmsMapper> mappers) {

		if(!lmsAuthorizationHelper.isLMSAuthTokenValid()) {
			lmsAuthToken = lmsAuthorizationHelper.getLMSAuthToken();	
		}

		JSONArray lmsCourseMapperArray =  new JSONArray();
		mappers.forEach(mapper -> lmsCourseMapperArray.put(getLMSCoursePayload(mapper)));

		JSONObject lmsCoursesPayload = new JSONObject();
		lmsCoursesPayload.put(GlobalConstants.OCN_COURSES, lmsCourseMapperArray);

		HttpHeaders headers = new HttpHeaders();
		headers.set(GlobalConstants.AUTHORIZATION_KEY, "Bearer " + lmsAuthToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<?> entity = new HttpEntity<Object>(lmsCoursesPayload, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(lmsPostCoursesApiUrl, HttpMethod.POST, entity, String.class);
			logger.info("response status : " + response.getStatusCode());
			logger.info("response status : " + response.getBody());
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
		}
	}

	private JSONObject getLMSCoursePayload(SFLmsMapper mapper) {
		JSONObject lmsMapper = new JSONObject();

		lmsMapper.put(GlobalConstants.COURSE_ID, mapper.getCourseID());
		lmsMapper.put(GlobalConstants.PROVIDER_ID, mapper.getProviderID());
		lmsMapper.put(GlobalConstants.STATUS, mapper.getStatus());
		lmsMapper.put(GlobalConstants.TITLE, getTitleOrDescription(mapper.getTitle().getLocale(), mapper.getTitle().getValue()));
		lmsMapper.put(GlobalConstants.CONTENT_DESCRIPTION, getTitleOrDescription(mapper.getDescription().getLocale(), mapper.getDescription().getValue()));
		lmsMapper.put(GlobalConstants.THUMBNAIL_URI, mapper.getThumbnailURI());
		lmsMapper.put(GlobalConstants.CONTENT, getLmsContent(mapper));

		return lmsMapper;
	}

	private JSONArray getTitleOrDescription(String locale, String value) {
		JSONObject requiredLmsFieldObject = new JSONObject();
		requiredLmsFieldObject.put(GlobalConstants.CONTENT_lOCALE, locale);
		requiredLmsFieldObject.put(GlobalConstants.CONTENT_VALUE, value);

		JSONArray requiredLmsFieldArray = new JSONArray();
		requiredLmsFieldArray.put(requiredLmsFieldObject);

		return requiredLmsFieldArray;	
	}

	private JSONArray getLmsContent(SFLmsMapper mapper) {
		JSONObject requiredLmsContentObject = new JSONObject();
		requiredLmsContentObject.put(GlobalConstants.PROVIDER_ID, mapper.getProviderID() );
		requiredLmsContentObject.put(GlobalConstants.LAUNCH_URL, mapper.getLaunchURL());
		requiredLmsContentObject.put(GlobalConstants.CONTENT_TITLE, mapper.getContentTitle() );
		requiredLmsContentObject.put(GlobalConstants.CONTENT_ID, mapper.getContentID());

		JSONArray requiredLmsContentArray = new JSONArray();
		requiredLmsContentArray.put(requiredLmsContentObject);

		return requiredLmsContentArray;
	}


}
