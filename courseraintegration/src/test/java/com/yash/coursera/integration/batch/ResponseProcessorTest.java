package com.yash.coursera.integration.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.helper.FileOpUtils;
import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.Elements;
import com.yash.coursera.integration.model.Instructor;
import com.yash.coursera.integration.model.Program;

@RunWith(SpringRunner.class)
public class ResponseProcessorTest {
	@InjectMocks
	private ResponseProcessor responseProcessor;

	@Mock
	private JobExecution jobExecution;
	
	@Mock
	private BatchConfig jobConfigurer;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private StepExecution stepExecution;

	@Mock
	private FileOpUtils fileOpUtils;

	@Mock
	private JobParameters jobParameters;
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	private Elements empltyElements;

	@Before
	public void setUp() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadContentAPI");
		responseProcessor.beforeStep(stepExecution);
	}
	@Test
	public void shouldNotProcessElements_WhenElementsNullInstance() throws Exception {
		empltyElements=new Elements();
		assertEquals(null, responseProcessor.process(empltyElements));
	}
	@Test
	public void shouldNotProcessElements_WhenElementsNull() throws Exception {
		assertEquals(null, responseProcessor.process(null));
	}
	@Test
	public void shouldProcessElements_WhenJobNameIsNotConetntAPI() throws Exception {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("testAPI");
		responseProcessor.beforeStep(stepExecution);
		assertNotNull(responseProcessor.process(getElements()));
		
	}
	@Test
	public void shouldProcessElements_WhenJobNameIsConetntAPI() throws Exception {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString("jobName")).thenReturn("loadContentAPI");
		responseProcessor.beforeStep(stepExecution);
		assertNotNull(responseProcessor.process(getElements()));
		
	}
	@Test
	public void shouldProcessElements_WhenInstructorsIsNull() throws Exception {
		
		assertNotNull(responseProcessor.process(getElementsWhereInstructorIsNull()));
		
	}
	@Test
	public void shouldProcessElements_WhenProgramsIsNull() throws Exception {
		
		assertNotNull(responseProcessor.process(getElementsWhereProgramIsNull()));
		
	}
	
	private Elements getElements() {
		Element element = new Element();
		element.setContentId("testContentId");
		element.setContentType("testContentType");
		element.setIsCompleted(true);
		element.setCompletedAt(new Date(System.currentTimeMillis()));
		element.setGrade("0.97");
		
		
		Program program =new Program();
		program.setContentUrl("http://testUrl.com");
		List<Program> listOfPrograms=new ArrayList<>();
		listOfPrograms.add(program);
		element.setPrograms(listOfPrograms);
		
		Instructor instructor=new Instructor();
		instructor.setPhotoUrl("http://testPhoto.img");
		List<Instructor> listOfInstructors=new ArrayList<>();
		listOfInstructors.add(instructor);
		element.setInstructors(listOfInstructors);
		
		List<Element> listOfElements = new ArrayList<>();
		listOfElements.add(element);
		Elements elements=new Elements();
		elements.setElement(listOfElements);
		return elements;
	}
	
	private Elements getElementsWhereInstructorIsNull() {
		Element element = new Element();
		element.setContentId("testContentId");
		element.setContentType("testContentType");
		element.setIsCompleted(true);
		element.setCompletedAt(new Date(System.currentTimeMillis()));
		element.setGrade("0.97");
		
		Program program =new Program();
		program.setContentUrl("http://testUrl.com");
		List<Program> listOfPrograms=new ArrayList<>();
		listOfPrograms.add(program);
		element.setPrograms(listOfPrograms);
		
		element.setInstructors(null);
		
		List<Element> listOfElements = new ArrayList<>();
		listOfElements.add(element);
		Elements elements=new Elements();
		elements.setElement(listOfElements);
		return elements;
	}
	
	private Elements getElementsWhereProgramIsNull() {
		Element element = new Element();
		element.setContentId("testContentId");
		element.setContentType("testContentType");
		element.setIsCompleted(true);
		element.setCompletedAt(new Date(System.currentTimeMillis()));
		element.setGrade("0.97");
		
		element.setPrograms(null);
		
		Instructor instructor=new Instructor();
		instructor.setPhotoUrl("http://testPhoto.img");
		List<Instructor> listOfInstructors=new ArrayList<>();
		listOfInstructors.add(instructor);
		element.setInstructors(listOfInstructors);
		
		List<Element> listOfElements = new ArrayList<>();
		listOfElements.add(element);
		Elements elements=new Elements();
		elements.setElement(listOfElements);
		return elements;
	}

}
