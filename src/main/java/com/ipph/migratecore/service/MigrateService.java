package com.ipph.migratecore.service;

import java.sql.SQLException;

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
	
	public void migrateTable(TableModel table){
		
		TableOperationEnum type=table.getType();
		switch (type){
			case MIGRATE :
				migrate(table);
				break;
			case UPDATE :
				update(table);
				break;
			default:
		}
	}
	
	private void migrate(TableModel table){
		
	}
	
	private void update(TableModel table){
		try {
			migrateDao.update(table);
		} catch (ConfigException |SQLException e) {
			e.printStackTrace();
		}
	}
}
