package com.yash.coursera.integration.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileOpUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileOpUtils.class);

	public void writeToFile(String[] str) {

		try {
			URL url = getResourceUrl();

			RandomAccessFile writer = new RandomAccessFile(url.getFile(), "rw");

			writer.seek(0);
			writer.writeBytes(str[0] + "\n");
			writer.writeBytes(str[1] + "\n");
			writer.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
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
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}

		return tokensMap;

	}

	public FileInputStream getFileInputStream(File fileLocal) throws FileNotFoundException {
		return new FileInputStream(fileLocal);

	}

	public BufferedInputStream getBufferedInputStream(InputStream inputStream) {
		return new BufferedInputStream(inputStream);

	}

	public OutputStream getFileOutputStream(File newFile) throws FileNotFoundException {
		return new FileOutputStream(newFile);

	}

	public BufferedOutputStream getBufferedOutputStream(OutputStream outputStream) {
		return new BufferedOutputStream(outputStream);

	}

	protected static URL getResourceUrl() {
		return FileOpUtils.class.getResource("/token.txt");
	}

}
