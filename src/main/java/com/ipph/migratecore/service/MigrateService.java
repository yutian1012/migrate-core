package com.ipph.migratecore.service;

import java.sql.SQLException;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.MigrateDao;
import com.ipph.migratecore.deal.exception.ConfigException;
import com.ipph.migratecore.enumeration.TableOperationEnum;
import com.ipph.migratecore.model.TableModel;

@Service
public class MigrateService {

	@Resource
	private MigrateDao migrateDao;
	
	public void migrateTable(TableModel table,Long batchLogId,Long parentLogId){
		
		TableOperationEnum type=table.getType();
		switch (type){
			case MIGRATE :
				migrate(table);
				break;
			case UPDATE :
				update(table,batchLogId,parentLogId);
				break;
			default:
		}
	}
	
	private void migrate(TableModel table){
		
	}
	
	private void update(TableModel table,Long batchLogId,Long parentLogId){
		try {
			migrateDao.update(table,batchLogId,parentLogId);
		} catch (ConfigException |SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取主键指定的记录
	 * @param table
	 * @param dataId
	 * @return
	 */
	public Map<String,Object> getRecord(TableModel table,Object dataId){
		return migrateDao.getRecord(table,dataId);
	}
}
