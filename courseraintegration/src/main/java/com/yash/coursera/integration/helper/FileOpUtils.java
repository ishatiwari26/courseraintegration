package com.yash.coursera.integration.helper;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.springframework.stereotype.Component;

@Component
public class FileOpUtils {

	public void writeToFile(String[] str) {

		try {
			URL url = getResourceUrl();

			RandomAccessFile writer = new RandomAccessFile(url.getFile(), "rw");

			writer.seek(0);
			writer.writeBytes(str[0] + "\n");
			writer.writeBytes(str[1] + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<String, String> readAccessToken() {

		Map<String, String> tokensMap = new HashMap<>();
		Scanner scanner;
		try {
			URL url = getResourceUrl();

			if (url != null) {
				File file = new File(url.getFile());
				scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					int index = line.indexOf("=");
					tokensMap.put(line.substring(0, index), line.substring(index + 1, line.length()));
				}

				scanner.close();
				return tokensMap;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tokensMap;

	}

	protected static URL getResourceUrl() {
		return FileOpUtils.class.getResource("/token.txt");
	}

}
