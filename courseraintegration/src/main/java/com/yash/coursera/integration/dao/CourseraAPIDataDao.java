package com.yash.coursera.integration.dao;

import java.util.List;

import com.yash.coursera.integration.model.SFLmsMapper;

public interface CourseraAPIDataDao {
	
	public void insertProgram(List<? extends SFLmsMapper> elements);
	public void insertContent(List<? extends SFLmsMapper> elements);
	public void insertStatus(List<? extends SFLmsMapper> elements);
	public void deleteProgram();
	public void deleteContent();
	public void deleteStatus();
	public List<String> getProgramIds();
}
