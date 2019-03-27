package com.yash.coursera.integration.model;

public class Instructor {

	private String photoUrl;
	private String name;
	private String title;
	private String department;

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Override
	public String toString() {
		return "Instructor [photoUrl=" + photoUrl + ", name=" + name + ", title=" + title + ", department=" + department
				+ "]";
	}

}
