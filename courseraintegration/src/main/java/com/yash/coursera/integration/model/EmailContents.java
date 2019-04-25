package com.yash.coursera.integration.model;

import java.sql.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailContents {
	private String mailSubject;
	private Date currentDate;
	private Integer totalInvitationCount;
	private Integer successInvitationCount;
	private Integer failedInvitationCount;
	private List<LMSUserDetails> listOfLMSUsers;
	
}
