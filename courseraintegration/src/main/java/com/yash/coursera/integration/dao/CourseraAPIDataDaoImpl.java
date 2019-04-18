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
	public void initialize() {
		setDataSource(dataSource);
	}

	@Override
	public void insertProgram(List<? extends SFLmsMapper> elements) {
		String sql = "insert into courseraintegration_schema.program "
				+ "(id, content_id,  title,provider_id, status, launch_url, created_date,thumbnail_uri ) values (?,?,?,?,?,?,?,?)";
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
				ps.setString(8, element.getThumbnailURI());
			}

			public int getBatchSize() {
				return elements.size();
			}
		});

	}

	@Override
	public void deleteProgram() {
		String sql = "delete from program";

		getJdbcTemplate().execute(sql);
	}


	@Override
	public void deleteContent() {
		String sql = "delete from content";
		getJdbcTemplate().execute(sql);
	}

	@Override
	public void insertContent(List<? extends SFLmsMapper> elements) {
		String sql = "insert into courseraintegration_schema.content "
				+ "(program_id,content_id, title,provider_id, status, launch_url, created_date,  description, thumbnail_uri ) values (?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().batchUpdate(sql, getBatchPreparedStatementSetter(elements));

	}

	protected BatchPreparedStatementSetter getBatchPreparedStatementSetter(List<? extends SFLmsMapper> elements) {
		return new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				SFLmsMapper element = elements.get(i);
				ps.setString(1, element.getCourseID());
				ps.setString(2, element.getContentID());
				ps.setString(3, element.getTitle().getValue());
				ps.setString(4, element.getStatus());
				ps.setString(5, element.getProviderID());
				ps.setString(6, element.getLaunchURL());
				ps.setDate(7, new Date(System.currentTimeMillis()));
				ps.setString(8, element.getDescription().getValue());
				ps.setString(9, element.getThumbnailURI());
			}

			public int getBatchSize() {
				return elements.size();
			}
		};
	}

	@Override
	public void insertStatus(List<? extends SFLmsMapper> elements) {
		String sql = "insert into courseraintegration_schema.status "
				+ "(userId, id, provider_id, isCompleted, completedAt, grade) values (?,?,?,?,?,?)";
		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				SFLmsMapper element = elements.get(i);
				ps.setString(1, element.getUserId());
				ps.setString(2, element.getId());
				ps.setString(3, element.getProviderID());
				ps.setBoolean(4, element.getIsCompleted());
				ps.setDate(5, element.getCompletedAt()); //new Date(System.currentTimeMillis())
				ps.setString(6, element.getGrade());
			}

			public int getBatchSize() {
				return elements.size();
			}
		});
	}

	@Override
	public void deleteStatus() {
		String sql = "delete from status";
		getJdbcTemplate().execute(sql);
	}


	@Override
	public List<String> getProgramIds() {

		String sql = "select id from courseraintegration_schema.program";
		List<String> programIds = getJdbcTemplate().queryForList(sql, String.class);

		return programIds;
	}

}
