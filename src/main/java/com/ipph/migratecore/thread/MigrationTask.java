package com.ipph.migratecore.thread;

import java.sql.SQLException;

import com.ipph.migratecore.dao.MigrateDao;
import com.ipph.migratecore.dao.MigrationDao;
import com.ipph.migratecore.deal.exception.ConfigException;
import com.ipph.migratecore.model.TableModel;

public class MigrationTask implements Runnable{
	
	private MigrationDao migrationDao;
	
	private TableModel table;
	
	private Long batchLogId;
	
	private Long parentLogId;
	
	private int start;
	
	private int size;
	
	public MigrationTask(MigrationDao migrationDao,TableModel table,Long batchLogId,Long parentLogId,int start,int size){
		this.migrationDao=migrationDao;
		this.table=table;
		this.start=start;
		this.size=size;
		this.batchLogId=batchLogId;
		this.parentLogId=parentLogId;
	}
	
	@Override
	public void run() {
		//migrateDao.migrateTable(table, start, size);
		
		if(null!=table) {
			switch (table.getType()){
    			case MIGRATE :
    				
    				break;
    			case UPDATE :
    				update();
    				break;
    			default:
    		}
		
		}
	}
	
	private void update() {
		try {
			migrationDao.update(table, batchLogId, parentLogId,start,size);
		} catch (ConfigException | SQLException e) {
			e.printStackTrace();
		}
	}
	
}
