package com.yash.coursera.integration.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.opencsv.CSVWriter;
import com.yash.coursera.integration.model.SFLmsMapper;

public class FileOpUtils {
	
		public static void writeToFile(String[] str) {

		// Get the file reference
		Path path = null;
		try {
			URL url = FileOpUtils.class.getResource("/token.txt");
			
			RandomAccessFile writer = new RandomAccessFile(url.getFile(), "rw");

			writer.seek(0);
		    writer.writeBytes(str[0] + "\n");
			writer.writeBytes(str[1] + "\n");
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Map<String, String> readAccessToken() {

		Map<String, String> tokensMap = new HashMap<>();
		Scanner scanner;
		try {
			URL url = FileOpUtils.class.getResource("/token.txt");

			if (url != null) {
				File file = new File(url.getFile());
				scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					int index = line.indexOf("=");
					tokensMap.put(line.substring(0,index), line.substring(index+1,line.length()));
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
	
	public static void exportToCsv(String filePath) {

        File file = new File(filePath);
      
        try {

               FileWriter outputfile = new FileWriter(file);
               CSVWriter writer = new CSVWriter(outputfile);

               String[] header = { "courseID", "providerID", "status", "title", "description", "thumbnailURI", "launchURL",
                       "contentTitle", "contentID" };
               
               writer.writeNext(header);

               
               List<SFLmsMapper> lmsDataList = SFLmsData.getInstance().getLmsDataList();

               for (SFLmsMapper sFLmsMapper : lmsDataList) {
            	   
            	
            		  String[]  lmsRowData = { sFLmsMapper.getCourseID(), sFLmsMapper.getProviderID(), "ACTIVE",
                               sFLmsMapper.getTitle().getLocale() + " " + sFLmsMapper.getTitle().getValue(),
                               sFLmsMapper.getDescription().getLocale() + " " + sFLmsMapper.getDescription().getValue(),
                               sFLmsMapper.getThumbnailURI(), sFLmsMapper.getLaunchURL(), sFLmsMapper.getContentTitle(),
                               sFLmsMapper.getContentID() }; 
                  
            	   
                     writer.writeNext(lmsRowData);

               }
               writer.close();
        } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
        }

 }


}
