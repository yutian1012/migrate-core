package com.ipph.migratecore.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.MigrateTableDao;
import com.ipph.migratecore.table.TableMetaModel;

/**
 * 源表和目标表的结构信息
 */
@Service
public class MigrateTableService {
	@Resource
	private MigrateTableDao migrateTableDao;
	
	
	public List<String> getSourceTables(){
		return migrateTableDao.getSourceTables();
	}
	
	public List<String> getTargetTables(){
		return migrateTableDao.getTargetTables();
	}
	
	public TableMetaModel getSourceTable(String tableName) {
		return migrateTableDao.getSourceTable(tableName);
	}
	
	public TableMetaModel getTargetTable(String tableName) {
		return migrateTableDao.getTargetTable(tableName);
	}
}
