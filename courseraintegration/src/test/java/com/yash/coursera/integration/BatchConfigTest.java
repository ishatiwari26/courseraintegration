package com.yash.coursera.integration;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.JobFlowBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.builder.TaskletStepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import com.yash.coursera.integration.batch.CustomDBWriter;
import com.yash.coursera.integration.batch.ResponseProcessor;
import com.yash.coursera.integration.batch.ResponseReader;
import com.yash.coursera.integration.components.CourseraTokenComponent;
import com.yash.coursera.integration.config.BatchConfig;
import com.yash.coursera.integration.dao.CourseraAPIDataDao;

@RunWith(MockitoJUnitRunner.class)
public class BatchConfigTest {

	@InjectMocks
	BatchConfig batchConfig;
	
	@Mock
	CourseraTokenComponent courseraTokenComponent;
	
	@Mock
	ResponseReader reader;
	
	@Mock
	ResponseProcessor processor;
	
	@Mock
	CustomDBWriter writer;
	
	@Mock
	CourseraAPIDataDao dao;
	
	@Mock
	private StepBuilderFactory stepBuilderFactory;

	@Mock
	private JobBuilderFactory jobBuilderFactory;

	@Mock
	private JobBuilder jobBuilder;
	
	@Mock
	private JobFlowBuilder jobFlowBuilder;
	
	@Mock
	private StepBuilder stepBuilder;
	
	@Mock
	private FlowJobBuilder flowBuilder;
	
	@Mock
	private Job job;

	@Mock
	private TaskletStepBuilder taskletStepBuilder;
	
	@Mock
	private TaskletStep taskletStep;
	
	@Mock
	private Step steps;
	
	@Mock
	private SimpleStepBuilder<Object, Object> simpleStepBuilder;
	
	@MockBean
	private ItemReader<Object> itemReader;
	
	@MockBean
	private ItemProcessor<Object, Object> itemProcessor;
	
	@MockBean
	private ItemWriter<Object> itemWriter;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setUp() {
		ReflectionTestUtils.setField(batchConfig, "limitCountPerRead", 100);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldProcessJob() {
		Mockito.when(stepBuilderFactory.get(Mockito.anyString())).thenReturn(stepBuilder);
		Mockito.when(stepBuilder.allowStartIfComplete(false)).thenReturn(stepBuilder);
		Mockito.when(stepBuilder.chunk(Mockito.anyInt())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.reader(Mockito.any())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.processor(Mockito.any(ItemProcessor.class))).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.writer(Mockito.any())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.build()).thenReturn(taskletStep);
		
		Mockito.when(jobBuilderFactory.get(Mockito.anyString())).thenReturn(jobBuilder);
		Mockito.when(jobBuilder.incrementer(Mockito.any(RunIdIncrementer.class))).thenReturn(jobBuilder);
		Mockito.when(jobBuilder.flow(Mockito.any())).thenReturn(jobFlowBuilder);
		Mockito.when(jobFlowBuilder.end()).thenReturn(flowBuilder);
		Mockito.when(flowBuilder.build()).thenReturn(job);
		Assert.assertEquals(job,batchConfig.processJob());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetJobSteps() {
		Mockito.when(stepBuilderFactory.get(Mockito.anyString())).thenReturn(stepBuilder);
		Mockito.when(stepBuilder.allowStartIfComplete(false)).thenReturn(stepBuilder);
		Mockito.when(stepBuilder.chunk(Mockito.anyInt())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.reader(Mockito.any())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.processor(Mockito.any(ItemProcessor.class))).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.writer(Mockito.any())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.build()).thenReturn(taskletStep);
		Assert.assertEquals(taskletStep,batchConfig.getStep());
	}
	/*@SuppressWarnings("unchecked")
	@Test
	public void shouldThorwException_WhenGettingJobSteps() {
//		exception.expect(MalformedURLException.class);
		Mockito.when(stepBuilderFactory.get(Mockito.anyString())).thenReturn(stepBuilder);
		Mockito.when(stepBuilder.allowStartIfComplete(false)).thenReturn(stepBuilder);
		Mockito.when(stepBuilder.chunk(Mockito.anyInt())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.reader(Mockito.any())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.processor(Mockito.any(ItemProcessor.class))).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.writer(Mockito.any())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.build()).thenReturn(taskletStep);
		batchConfig.getStep();
	}*/
	
	@Test
	public void shouldprocessInviteJob() {
		Mockito.when(stepBuilderFactory.get(Mockito.anyString())).thenReturn(stepBuilder);
		Mockito.when(stepBuilder.allowStartIfComplete(false)).thenReturn(stepBuilder);
		Mockito.when(stepBuilder.chunk(Mockito.anyInt())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.reader(itemReader)).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.processor(itemProcessor)).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.writer(itemWriter)).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.build()).thenReturn(taskletStep);
		
		Mockito.when(jobBuilderFactory.get(Mockito.anyString())).thenReturn(jobBuilder);
		Mockito.when(jobBuilder.incrementer(Mockito.any(RunIdIncrementer.class))).thenReturn(jobBuilder);
		Mockito.when(jobBuilder.flow(Mockito.any())).thenReturn(jobFlowBuilder);
		Mockito.when(jobFlowBuilder.end()).thenReturn(flowBuilder);
		Mockito.when(flowBuilder.build()).thenReturn(job);
		Assert.assertEquals(job,batchConfig.processInviteJob());
	}
	
	@Test
	public void shouldSendInviteStep() {
		Mockito.when(stepBuilderFactory.get(Mockito.anyString())).thenReturn(stepBuilder);
		Mockito.when(stepBuilder.allowStartIfComplete(false)).thenReturn(stepBuilder);
		Mockito.when(stepBuilder.chunk(Mockito.anyInt())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.reader(itemReader)).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.processor(itemProcessor)).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.writer(itemWriter)).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.build()).thenReturn(taskletStep);
		Assert.assertEquals(taskletStep,batchConfig.inviteStep());
	}
	/*@Test
	public void shouldThorwException_WhenSendInviteStep() {
//		exception.expect(MalformedURLException.class);
		Mockito.when(stepBuilderFactory.get(Mockito.anyString())).thenReturn(stepBuilder);
		Mockito.when(stepBuilder.allowStartIfComplete(false)).thenReturn(stepBuilder);
		Mockito.when(stepBuilder.chunk(Mockito.anyInt())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.reader(Mockito.any())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.processor(itemProcessor)).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.writer(Mockito.any())).thenReturn(simpleStepBuilder);
		Mockito.when(simpleStepBuilder.build()).thenReturn(taskletStep);
		batchConfig.sendInviteStep();
	}*/
	/*@Test
	public void shouldThorwException_WhenSendInviteStep() throws MalformedURLException, IOException {
//		URLConnection connection=new URL("http://bit.ly/15sYPA7").openConnection();
		reader.setJobConfigurer(batchConfig);
		reader.setLimitCountPerRead(10000);
		
	}*/
	@Test
	public void shouldGetNewToken() {
		String token="testToken";
		Mockito.when(courseraTokenComponent.getNewAccessToken(Mockito.anyString())).thenReturn(token);
		batchConfig.getNewToken(Mockito.anyString());
	}
}
