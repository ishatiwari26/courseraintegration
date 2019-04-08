package com.yash.coursera.integration.batch;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.yash.coursera.integration.dao.CourseraAPIDataDao;
import com.yash.coursera.integration.model.SFLmsMapper;

@Component
public class CustomDBWriter implements ItemWriter<List<SFLmsMapper>> {

	private CourseraAPIDataDao courseraAPIDataDao;
	private JobExecution jobExecution;
	private String jobName;

	public CustomDBWriter(CourseraAPIDataDao courseraAPIDataDao) {
		this.courseraAPIDataDao = courseraAPIDataDao;
	}

	public CustomDBWriter() {
	}

	public CourseraAPIDataDao getDao() {
		return courseraAPIDataDao;
	}
	
	public void setDao(CourseraAPIDataDao courseraAPIDataDao) {
		this.courseraAPIDataDao = courseraAPIDataDao;
	}
	@Override
	public void write(List<? extends List<SFLmsMapper>> mappers) throws Exception {
		System.out.println("mappers.get(0) size>>>>" + mappers.get(0).size());
		if (jobName.equals("loadProgramAPI"))
			courseraAPIDataDao.insertProgram(mappers.get(0));
		else if (jobName.equals("loadContentAPI"))
			courseraAPIDataDao.insertContent(mappers.get(0));
		else // if(jobName.equals("loadStatusAPI"))
			courseraAPIDataDao.insertStatus(mappers.get(0));
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		jobExecution = stepExecution.getJobExecution();
		jobName = jobExecution.getJobParameters().getString("jobName");
		if (jobName.equals("loadProgramAPI"))
			courseraAPIDataDao.deleteProgram();
		else if (jobName.equals("loadContentAPI"))
			courseraAPIDataDao.deleteContent();
		else // if(jobName.equals("loadStatusAPI"))
			courseraAPIDataDao.deleteStatus();
	}

	
	
}
