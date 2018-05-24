package com.ipph.migratecore.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceConfig {
    
    //数据源配置
    @Bean(name = "sourceDataSource")
    @Qualifier("sourceDataSource")
    @ConfigurationProperties(prefix="spring.datasource.source")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "destDataSource")
    @Qualifier("destDataSource")
    @ConfigurationProperties(prefix="spring.datasource.dest")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    //@Bean(name = "migrateDataSource")
    //@Qualifier("migrateDataSource")
    @Primary
    @Bean
    @ConfigurationProperties(prefix="spring.datasource.migrate")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
    
    //jdbc匹配相应的数据源
    @Bean(name = "sourceJdbcTemplate")
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("sourceDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "destJdbcTemplate")
    public JdbcTemplate secondaryJdbcTemplate(@Qualifier("destDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    //@Bean(name="migrateJdbcTemplate")
    //public JdbcTemplate migrateJdbcTemplate(@Qualifier("migrateDataSource")DataSource dataSource) {
    @Bean
    @Primary
    public JdbcTemplate migrateJdbcTemplate(DataSource dataSource) {
    	return new JdbcTemplate(dataSource);
    }
}