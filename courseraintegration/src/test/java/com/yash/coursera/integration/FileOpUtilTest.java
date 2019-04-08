package com.yash.coursera.integration;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.yash.coursera.integration.helper.FileOpUtils;

public class FileOpUtilTest {
	
	
	@Test
	public void readAccessToken() {
		FileOpUtils fileOpUtil =  new FileOpUtils();
		Map<String, String> map = fileOpUtil.readAccessToken();
		assertEquals("test", "test");
	}
	
	@Test
	public void writeAccessToken() {
		FileOpUtils fileOpUtil =  new FileOpUtils();
		fileOpUtil.writeToFile(new String[] {"access_token=test","refresh_token=test"});
	}


}
