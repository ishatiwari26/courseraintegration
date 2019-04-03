package com.yash.coursera.integration.batch;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.AbstractSqlPagingQueryProvider;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.yash.coursera.integration.model.User;

@Configuration
public class InviteReader extends JdbcDaoSupport{

	private final static String SELECT_CLAUSE = "SELECT *";
	private final static String FROM_CLAUSE = "FROM courseraintegration_schema.user";

	@Autowired
	DataSource dataSource;

	@PostConstruct
	private void initialize() {
		setDataSource(dataSource);
	}
	
	@Bean
	public ItemReader<User> userReader(DataSource dataSource) throws Exception {
		JdbcPagingItemReader<User> userReader = new JdbcPagingItemReader<>();

		userReader.setDataSource(dataSource);
		userReader.setPageSize(2);

		PagingQueryProvider queryProvider = createQueryProvider(dataSource);
		userReader.setQueryProvider(queryProvider);

		userReader.setRowMapper(new BeanPropertyRowMapper<>(User.class));

		return userReader;
	}

	private PagingQueryProvider createQueryProvider(DataSource dataSource) throws Exception {

		AbstractSqlPagingQueryProvider  queryProvider = new PostgresPagingQueryProvider();
		queryProvider.setSelectClause(SELECT_CLAUSE);
		queryProvider.setFromClause(FROM_CLAUSE);
		queryProvider.setSortKeys(sortByIdAsc());
		return queryProvider;
	}

	private Map<String, Order> sortByIdAsc() {
		Map<String, Order> sortConfiguration = new HashMap<>();
		sortConfiguration.put("id", Order.ASCENDING);
		return sortConfiguration;
	}
	
	

}
