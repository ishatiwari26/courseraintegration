package com.yash.coursera.integration.config;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchDatasourceConfig extends DefaultBatchConfigurer {   

    @Override
    public void setDataSource(DataSource dataSource) {
        //This BatchConfigurer ignores any DataSource
    }
}
