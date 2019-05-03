package com.yash.coursera.integration.helper;

import static org.junit.Assert.assertEquals;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.yash.coursera.integration.helper.FileOpUtils;

@RunWith(MockitoJUnitRunner.class)
public class FileOpUtilTest {
	
	@InjectMocks
	private FileOpUtils fileOpUtils;
	
	@Test
	public void readAccessToken() {
		Map<String, String> map = fileOpUtils.readAccessToken();
		assertEquals("test", map.get("access_token"));
		assertEquals("test", map.get("refresh_token"));
	}
	
	@Test
	public void writeAccessToken() {
		fileOpUtils.writeToFile(new String[] {"access_token=test","refresh_token=test"});
	}


}
