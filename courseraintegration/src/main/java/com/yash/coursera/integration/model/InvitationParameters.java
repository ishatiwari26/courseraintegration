package com.yash.coursera.integration.model;

import java.util.List;
import java.util.Map;

public class InvitationParameters {

	private List<String> programIds;
	private Boolean flag;
	private List<String> successfulUserInvite;
	private Map<String, String> unsuccessfulUserInvite;

	public List<String> getProgramIds() {
		return programIds;
	}

	public void setProgramIds(List<String> programIds) {
		this.programIds = programIds;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public List<String> getSuccessfulUserInvite() {
		return successfulUserInvite;
	}

	public void setSuccessfulUserInvite(List<String> successfulUserInvite) {
		this.successfulUserInvite = successfulUserInvite;
	}

	public Map<String, String> getUnsuccessfulUserInvite() {
		return unsuccessfulUserInvite;
	}

	public void setUnsuccessfulUserInvite(Map<String, String> unsuccessfulUserInvite) {
		this.unsuccessfulUserInvite = unsuccessfulUserInvite;
	}

}
