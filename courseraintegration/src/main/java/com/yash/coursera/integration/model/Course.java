package com.yash.coursera.integration.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Course {

	private List<Content> courseIds;
	private String tagline;
}
