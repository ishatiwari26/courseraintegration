package com.yash.coursera.integration.model;

import java.sql.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Element {

	private String id;
	private String tagline;
	private String name;
	private String url;
	private String contentId;
	private String contentType;
	private String description;
	private String languageCode;
	private Object extraMetadata;
	private Object partners;

	private List<Content> contentIds;
	private List<Program> programs;
	private List<Instructor> instructors;
	
	private String fullName;
	private String externalId;
	private String email;
	private String programId;
	
	private String userId;
	private Boolean isCompleted;
	private Date completedAt;
	private String grade;

}
