package com.ipph.migratecore.table;

import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTemplateUtil {

	/**
	 * 使用JdbcTemplate 执行SQL语句。
	 * 
	 * @param sql
	 */
	public static JdbcTemplate getSourceJdbcTemplate(String name) {
		return (JdbcTemplate) AppUtil.getBean(name);
	}
}