package com.ipph.migratecore.table;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MySqlSourceTableMeta extends MySqlTableMeta{
	
	@Resource(name="sourceJdbcTemplate")
	private JdbcTemplate sourceJdbcTemplate;
		
	@Override
	protected JdbcTemplate getJdbcTemplate() {
		return sourceJdbcTemplate;
	}
	
}
