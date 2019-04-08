package com.yash.coursera.integration.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonUtils {
	
	@Autowired
	FileOpUtils fileOpUtils;

	public void writeToFile(String accessToken, String refreshToken) {
		String[] str = new String[] { GlobalConstants.ACCESS_TOKEN_KEY + "=" + accessToken,
				GlobalConstants.REFRESH_TOKEN_KEY + "=" + refreshToken };
		fileOpUtils.writeToFile(str);
	}

}
