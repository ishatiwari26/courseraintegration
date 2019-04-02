package com.yash.coursera.integration.batch;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

import com.yash.coursera.integration.dao.CourseraAPIDataDao;
import com.yash.coursera.integration.model.SFLmsMapper;

public class CustomDBWriter implements ItemWriter<List<SFLmsMapper>> {

	private final CourseraAPIDataDao dao;
	private JobExecution jobExecution;
	private String jobName;

	public CustomDBWriter(CourseraAPIDataDao dao) {
		this.dao = dao;
	}

	@Override
	public void write(List<? extends List<SFLmsMapper>> mappers) throws Exception {
		System.out.println("mappers.get(0) size>>>>"+mappers.get(0).size());
		if(jobName.equals("loadProgramAPI"))
			dao.insertProgram(mappers.get(0));
		else 
			dao.insertContent(mappers.get(0));
	}
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		jobExecution = stepExecution.getJobExecution();
		jobName = jobExecution.getJobParameters().getString("jobName");
	}
}
