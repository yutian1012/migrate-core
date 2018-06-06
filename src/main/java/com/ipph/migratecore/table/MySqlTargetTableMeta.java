package com.ipph.migratecore.table;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MySqlTargetTableMeta extends MySqlTableMeta{
	
	@Resource(name="destJdbcTemplate")
	private JdbcTemplate destJdbcTemplate;

	@Override
	protected JdbcTemplate getJdbcTemplate() {
		return destJdbcTemplate;
	}

	
}
