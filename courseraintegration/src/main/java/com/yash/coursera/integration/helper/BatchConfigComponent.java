package com.yash.coursera.integration.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yash.coursera.integration.service.CourseraService;

@Component
public class BatchConfigComponent {
	@Autowired
	private CourseraService courseraService;

	public String callGetNewAccessToken(String refreshToken) {
		return courseraService.getNewAccessToken(refreshToken);
	}

}
