package com.yash.coursera.integration.model;

public class Program {

	private String contentUrl;
	private String programId;

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	@Override
	public String toString() {
		return "Program [contentUrl=" + contentUrl + ", programId=" + programId + "]";
	}

}
