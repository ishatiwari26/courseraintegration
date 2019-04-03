package com.yash.coursera.integration.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {

	private Integer id;
	private String externalId;
	private String fullName;
	private String email;
}
