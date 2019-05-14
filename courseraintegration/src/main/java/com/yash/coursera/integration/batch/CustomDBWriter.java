package com.yash.coursera.integration.batch;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yash.coursera.integration.components.LMSPostStatusComponent;
import com.yash.coursera.integration.dao.CourseraAPIDataDao;
import com.yash.coursera.integration.model.SFLmsMapper;

@Component
public class CustomDBWriter implements ItemWriter<List<SFLmsMapper>> {

	@Autowired
	private CourseraAPIDataDao courseraAPIDataDao;
	@Autowired
	private LMSPostStatusComponent lmsPostStatusComponent;
	private JobExecution jobExecution;
	private String jobName;

	/*public CustomDBWriter(CourseraAPIDataDao courseraAPIDataDao) {
		this.courseraAPIDataDao = courseraAPIDataDao;
	}

	public CustomDBWriter() {
	}

	public CourseraAPIDataDao getDao() {
		return courseraAPIDataDao;
	}*/
	
	public void setDao(CourseraAPIDataDao courseraAPIDataDao) {
		this.courseraAPIDataDao = courseraAPIDataDao;
	}
	@Override
	public void write(List<? extends List<SFLmsMapper>> sfLMSMappers) throws Exception {
		try{
		if (jobName.equals("loadProgramAPI"))
			courseraAPIDataDao.insertProgram(sfLMSMappers.get(0));
		else if (jobName.equals("loadContentAPI"))
			courseraAPIDataDao.insertContent(sfLMSMappers.get(0));
		else 
//			courseraAPIDataDao.insertStatus(sfLMSMappers.get(0));
			lmsPostStatusComponent.postLMSCoursesStatus(sfLMSMappers.get(0));
			
		}
		catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		jobExecution = stepExecution.getJobExecution();
		jobName = jobExecution.getJobParameters().getString("jobName");
		if (jobName.equals("loadProgramAPI"))
			courseraAPIDataDao.deleteProgram();
		else if (jobName.equals("loadContentAPI"))
			courseraAPIDataDao.deleteContent();
		else 
			courseraAPIDataDao.deleteStatus();
	}

	
	
}
