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
import com.yash.coursera.integration.model.Content;
import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.Instructor;
import com.yash.coursera.integration.model.Program;
import com.yash.coursera.integration.model.SFLmsMapper;
import com.yash.coursera.integration.model.Title;

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
		List<? extends List<SFLmsMapper>> listOfSfLMSMappersList = new ArrayList<>();
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadProgramAPI");
		doNothing().when(courseraAPIDataDao).deleteProgram();
		customDBWriter.beforeStep(stepExecution);
		doThrow(new IndexOutOfBoundsException("Index: 0, Size: 0")).when(courseraAPIDataDao)
				.insertProgram(Mockito.any());
		customDBWriter.write(listOfSfLMSMappersList);
	}

	@Test
	public void shouldThrowIndexOutOfBoundException_ForContentAPI_WhenInsertNullSfLMSMappers() throws Exception {
		List<? extends List<SFLmsMapper>> listOfSfLMSMappersList = new ArrayList<>();
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadContentAPI");
		doNothing().when(courseraAPIDataDao).deleteContent();
		customDBWriter.beforeStep(stepExecution);
		doThrow(new IndexOutOfBoundsException("Index: 0, Size: 0")).when(courseraAPIDataDao)
				.insertContent(Mockito.any());
		customDBWriter.write(listOfSfLMSMappersList);
	}

	@Test
	public void shouldThrowIndexOutOfBoundException_ForStatusAPI_WhenInsertNullSfLMSMappers() throws Exception {
		List<? extends List<SFLmsMapper>> listOfSfLMSMappersList = new ArrayList<>();
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadStatusAPI");
		doNothing().when(courseraAPIDataDao).deleteStatus();
		customDBWriter.beforeStep(stepExecution);
		doThrow(new IndexOutOfBoundsException("Index: 0, Size: 0")).when(courseraAPIDataDao)
				.insertStatus(Mockito.any());
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
		Element element = new Element();
		element.setContentId("testContentId");
		element.setContentType("testContentType");
		element.setId("testId");
		element.setTagline("testTagline");
		element.setName("testName");
		element.setUrl("testUrl");
		element.setPartners("testPartners");
		element.setDescription("testDescription");
		element.setLanguageCode("testLanguageCode");
		element.setFullName("testFullName");
		element.setExternalId("testExternalId");
		element.setEmail("testEmail");
		element.setUserId("testUserId");
		element.setIsCompleted(false);
		element.setCompletedAt(new Date(System.currentTimeMillis()));
		element.setGrade("testGrade");
		
		Instructor instructor=new Instructor();
		instructor.setDepartment("testDepartment");
		instructor.setName("testInstructorName");
		instructor.setPhotoUrl("http://testImage.img");
		instructor.setTitle("textTitle");
		
		List<Instructor> listOfInstructor=new ArrayList<>();
		listOfInstructor.add(instructor);
		
		Content content=new Content();
		content.setContentId("testContentID");
		content.setContentType("testContentType");
		
		List<Content> listOfContent=new ArrayList<>();
		listOfContent.add(content);
		Program program=new Program();
		program.setContentUrl("http://testContentUrl.com");
		program.setProgramId("testProgramId");
		List<Program> listOfProgram =new ArrayList<>();
		listOfProgram.add(program);
		
		Object extraMetadata=new Object();
		Object partners=new Object();
		
		element.setInstructors(listOfInstructor);
		element.setContentIds(listOfContent);
		element.setPrograms(listOfProgram);
		element.setExtraMetadata(extraMetadata);
		element.setPartners(partners);
		
		
		List<Element> listOfElements = new ArrayList<>();
		listOfElements.add(element);
		
		List<List<SFLmsMapper>> listOfSfLMSMappersList = new ArrayList<>();

		List<SFLmsMapper> testSfLMSMappers = new ArrayList<SFLmsMapper>();
		
		SFLmsMapper testSFLmsMapper = new SFLmsMapper();
		
		testSFLmsMapper.setContentID(element.getContentId());
		testSFLmsMapper.setContentTitle(element.getContentType());
		testSFLmsMapper.setProviderID("YASH");
		testSFLmsMapper.setStatus("ACTIVE");

		testSFLmsMapper.setId(element.getId());
		testSFLmsMapper.setUserId(element.getUserId());
		testSFLmsMapper.setIsCompleted(element.getIsCompleted());
		testSFLmsMapper.setCompletedAt(element.getCompletedAt());
		testSFLmsMapper.setGrade(element.getGrade());
		Title title=new Title("Hn", "testValue");
		title.setLocale("Hn");
		title.setValue("testValue");
		testSFLmsMapper.setDescription(title);
		testSFLmsMapper.setThumbnailURI(element.getInstructors().get(0).getPhotoUrl());
		testSFLmsMapper.setLaunchURL(element.getPrograms().get(0).getContentUrl());

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

		testSfLMSMappers.stream().forEach((sfLmsMapper) -> {
			listOfSfLMSMappersList.add(testSfLMSMappers);
			listOfSfLMSMappersList.add(testSfLMSMappers1);
		});

		return listOfSfLMSMappersList;
	}
}
