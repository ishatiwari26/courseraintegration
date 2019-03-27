package com.yash.coursera.integration.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SFLmsMapper {

	private String courseID;
	private String providerID;
	private String status;
	private Title title;
	private Title description;
	private String thumbnailURI;
	private String launchURL;
	private String contentTitle;
	private String contentID;
	
}
