package com.yash.coursera.integration.dao;

import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.yash.coursera.integration.dao.CourseraAPIDataDaoImpl;
import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.Elements;
import com.yash.coursera.integration.model.SFLmsMapper;

@RunWith(MockitoJUnitRunner.class)
public class CourseraAPIDataDaoTest {

	@InjectMocks
	private CourseraAPIDataDaoImpl courseraAPIDataDaoImpl;
	@Mock
	private JdbcTemplate jdbcTemplate;
	@Mock
	private DataSource dataSource;
	@Mock
	private PreparedStatement preparedStattement;
	@Mock
	private Connection connection;
	@Mock
	private CallableStatement callableStatement;
	@Mock
	private JdbcDaoSupport jdbcDaoSupport;
	@Mock	
	private Element element;
	@Mock
	private Elements listOfElements;
	@Mock
	private SFLmsMapper sfLmsMapper;
	@Mock
	private BatchPreparedStatementSetter batchPreparedStatementSetter;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	  public void setup() {
		 MockitoAnnotations.initMocks(this);		 
		 courseraAPIDataDaoImpl.initialize();
	  }

	@Test
	public void shouldInsertProgram() throws SQLException {
			List<SFLmsMapper> listOfSfLmsMapper=new ArrayList<>();
			listOfSfLmsMapper.add(sfLmsMapper);
		    int[]  returnValue = {1,2,3};
		    when(jdbcTemplate.batchUpdate(Mockito.anyString(), Mockito.any(BatchPreparedStatementSetter.class))).thenReturn(returnValue);

		    courseraAPIDataDaoImpl.insertProgram(listOfSfLmsMapper);
	}
	@Test
	public void shouldInsertContent() throws SQLException {
		List<SFLmsMapper> listOfSfLmsMapper=new ArrayList<>();
		listOfSfLmsMapper.add(sfLmsMapper);
		int[]  returnValue = {1,2,3};
		when(jdbcTemplate.batchUpdate(Mockito.anyString(), Mockito.any(BatchPreparedStatementSetter.class))).thenReturn(returnValue);
		
		courseraAPIDataDaoImpl.insertContent(listOfSfLmsMapper);
	}
	@Test
	public void shouldInsertStatus() throws SQLException {
		List<SFLmsMapper> listOfSfLmsMapper=new ArrayList<>();
		listOfSfLmsMapper.add(sfLmsMapper);
		int[]  returnValue = {1,2,3};
		when(jdbcTemplate.batchUpdate(Mockito.anyString(), Mockito.any(BatchPreparedStatementSetter.class))).thenReturn(returnValue);
		
		courseraAPIDataDaoImpl.insertStatus(listOfSfLmsMapper);
	}
}
