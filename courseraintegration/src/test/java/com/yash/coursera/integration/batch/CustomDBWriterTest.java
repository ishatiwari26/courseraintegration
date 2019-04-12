package com.yash.coursera.integration.batch;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.test.context.junit4.SpringRunner;

import com.yash.coursera.integration.dao.CourseraAPIDataDao;
import com.yash.coursera.integration.model.SFLmsMapper;

@RunWith(SpringRunner.class)
public class CustomDBWriterTest {

	@InjectMocks
	private CustomDBWriter customDBWriter;

	@Mock
	private CourseraAPIDataDao courseraAPIDataDao;

	@Mock
	private JobExecution jobExecution;

	@Mock
	private StepExecution stepExecution;

	@Mock
	private JobParameters jobParameters;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	@Before
	public void setUp() {
		customDBWriter.setDao(courseraAPIDataDao);	
	}
	
	@Test
	public void shouldExcuteBeforeStep_WhenJobNameIsLoadProgramAPI() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadProgramAPI");
		doNothing().when(courseraAPIDataDao).deleteProgram();
		customDBWriter.beforeStep(stepExecution);
	}
	
	@Test
	public void shouldExcuteBeforeStep_WhenJobNameIsLoadContentAPI() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadContentAPI");
		doNothing().when(courseraAPIDataDao).deleteContent();
		customDBWriter.beforeStep(stepExecution);
	}
	@Test
	public void shouldExcuteBeforeStep_WhenJobNameIsLoadStatusAPI() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadStatusAPI");
		doNothing().when(courseraAPIDataDao).deleteStatus();
		customDBWriter.beforeStep(stepExecution);
	}

	@Test
	public void shouldThrowIndexOutOfBoundException_ForProgramAPI_WhenInsertNullSfLMSMappers() throws Exception {
		List<? extends List<SFLmsMapper>> listOfSfLMSMappersList=new ArrayList<>();
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadProgramAPI");
		doNothing().when(courseraAPIDataDao).deleteProgram();
		customDBWriter.beforeStep(stepExecution);
		doThrow(new IndexOutOfBoundsException("Index: 0, Size: 0")).when(courseraAPIDataDao).insertProgram(Mockito.any());
		customDBWriter.write(listOfSfLMSMappersList);
	}
	@Test
	public void shouldThrowIndexOutOfBoundException_ForContentAPI_WhenInsertNullSfLMSMappers() throws Exception {
		List<? extends List<SFLmsMapper>> listOfSfLMSMappersList=new ArrayList<>();
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadContentAPI");
		doNothing().when(courseraAPIDataDao).deleteContent();
		customDBWriter.beforeStep(stepExecution);
		doThrow(new IndexOutOfBoundsException("Index: 0, Size: 0")).when(courseraAPIDataDao).insertContent(Mockito.any());
		customDBWriter.write(listOfSfLMSMappersList);
	}
	@Test
	public void shouldThrowIndexOutOfBoundException_ForStatusAPI_WhenInsertNullSfLMSMappers() throws Exception {
		List<? extends List<SFLmsMapper>> listOfSfLMSMappersList=new ArrayList<>();
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadStatusAPI");
		doNothing().when(courseraAPIDataDao).deleteStatus();
		customDBWriter.beforeStep(stepExecution);
		doThrow(new IndexOutOfBoundsException("Index: 0, Size: 0")).when(courseraAPIDataDao).insertStatus(Mockito.any());
		customDBWriter.write(listOfSfLMSMappersList);
	}
	
	@Test
	public void shouldWriteToDB_ForProgramAPI() throws Exception {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadProgramAPI");
		doNothing().when(courseraAPIDataDao).deleteProgram();
		customDBWriter.beforeStep(stepExecution);
		doNothing().when(courseraAPIDataDao).insertProgram(Mockito.any());
		customDBWriter.write(getElementsOfSfLMSMappers());
	}
	@Test
	public void shouldWriteToDB_ForContentAPI() throws Exception {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadContentAPI");
		doNothing().when(courseraAPIDataDao).deleteContent();
		customDBWriter.beforeStep(stepExecution);
		doNothing().when(courseraAPIDataDao).insertContent(Mockito.any());
		customDBWriter.write(getElementsOfSfLMSMappers());
	}
	@Test
	public void shouldWriteToDB_ForStatusAPI() throws Exception {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadStatusAPI");
		doNothing().when(courseraAPIDataDao).deleteStatus();
		customDBWriter.beforeStep(stepExecution);
		doNothing().when(courseraAPIDataDao).insertStatus(Mockito.any());
		customDBWriter.write(getElementsOfSfLMSMappers());
	}

	private List<List<SFLmsMapper>> getElementsOfSfLMSMappers() {
		List<List<SFLmsMapper>> listOfSfLMSMappersList=new ArrayList<>();
		
		List<SFLmsMapper> testSfLMSMappers = new ArrayList<SFLmsMapper>();
		SFLmsMapper testSFLmsMapper = new SFLmsMapper();
		testSFLmsMapper.setContentID("testContentID");
		testSFLmsMapper.setContentTitle("testContentType");
		testSFLmsMapper.setProviderID("YASH");
		testSFLmsMapper.setStatus("ACTIVE");

		testSFLmsMapper.setId("testId");
		testSFLmsMapper.setUserId("testUserId");
		testSFLmsMapper.setIsCompleted(false);
		testSFLmsMapper.setCompletedAt(new Date(System.currentTimeMillis()));
		testSFLmsMapper.setGrade("testGrade");

		testSfLMSMappers.add(testSFLmsMapper);
		
		List<SFLmsMapper> testSfLMSMappers1 = new ArrayList<SFLmsMapper>();
		SFLmsMapper testSFLmsMapper1 = new SFLmsMapper();
		testSFLmsMapper1.setContentID("testContentID_test");
		testSFLmsMapper1.setContentTitle("testContentType_test");
		testSFLmsMapper1.setProviderID("testYASH");
		testSFLmsMapper1.setStatus("testACTIVE");
		
		testSFLmsMapper1.setId("testId_test");
		testSFLmsMapper1.setUserId("testUserId_test");
		testSFLmsMapper1.setIsCompleted(false);
		testSFLmsMapper1.setCompletedAt(new Date(System.currentTimeMillis()));
		testSFLmsMapper1.setGrade("testGrade_test");
		
		testSfLMSMappers1.add(testSFLmsMapper1);
		
		listOfSfLMSMappersList.add(testSfLMSMappers);
		listOfSfLMSMappersList.add(testSfLMSMappers1);
		
		return listOfSfLMSMappersList;
	}
}
