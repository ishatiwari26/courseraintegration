package com.yash.coursera.integration.helper;

import org.springframework.stereotype.Component;

@Component
public class CommonUtils {
	
	public void writeToFile(String accessToken, String refreshToken) {
		String[] str = new String[] { GlobalConstants.ACCESS_TOKEN_KEY +"="+ accessToken,
				GlobalConstants.REFRESH_TOKEN_KEY +"="+ refreshToken };
		FileOpUtils.writeToFile(str);
	}

	
}
