package com.yash.coursera.integration.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.yash.coursera.integration.model.SFLmsMapper;

@Repository
public class CourseraAPIDataDaoImpl extends JdbcDaoSupport implements CourseraAPIDataDao {

	@Autowired
	DataSource dataSource;

	@PostConstruct
	private void initialize() {
		setDataSource(dataSource);
	}

	@Override
	public void insertProgram(List<? extends SFLmsMapper> elements) {
		String sql = "insert into courseraintegration_schema.program "
				+ "(id, content_id,  title,provider_id, status, launch_url, created_date ) values (?,?,?,?,?,?,?)";
		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				SFLmsMapper element = elements.get(i);
				ps.setString(1, element.getCourseID());
				ps.setString(2, element.getContentID());
				ps.setString(3, element.getTitle().getValue());
				ps.setString(4, element.getStatus());
				ps.setString(5, element.getProviderID());
				ps.setString(6, element.getLaunchURL());
				ps.setDate(7, new Date(System.currentTimeMillis()));
			}

			public int getBatchSize() {
				return elements.size();
			}
		});

	}
	
	
	@Override
	public void insertContent(List<? extends SFLmsMapper> elements) {
		String sql = "insert into courseraintegration_schema.content "
				+ "(program_id,content_id, title,provider_id, status, launch_url, created_date,  description ) values (?,?,?,?,?,?,?,?)";
		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				SFLmsMapper element = elements.get(i);
				ps.setString(1, element.getCourseID());
				System.out.println(" element.getContentID()>>>"+ element.getContentID());
				ps.setString(2, element.getContentID());
				ps.setString(3, element.getTitle().getValue());
				ps.setString(4, element.getStatus());
				ps.setString(5, element.getProviderID());
				ps.setString(6, element.getLaunchURL());
				ps.setDate(7, new Date(System.currentTimeMillis()));
				ps.setString(8, element.getDescription().getValue());
			}

			public int getBatchSize() {
				return elements.size();
			}
		});

	}

}
