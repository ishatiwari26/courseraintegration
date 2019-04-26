package com.yash.coursera.integration.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailContents {
	private String mailSubject;
	private LocalDate currentDate;
	private Integer totalInvitationCount;
	private Integer successInvitationCount;
	private Integer failedInvitationCount;

}
