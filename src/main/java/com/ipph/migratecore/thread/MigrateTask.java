package com.ipph.migratecore.thread;

import com.ipph.migratecore.dao.MigrateDao;
import com.ipph.migratecore.model.MigrateModel;

public class MigrateTask implements Runnable{
	
	private MigrateModel migrateModel;
	
	private MigrateDao migrateDao;
	
	public MigrateTask(MigrateDao migrateDao,MigrateModel migrateModel) {
		this.migrateDao=migrateDao;
		this.migrateModel=migrateModel;
	}

	@Override
	public void run() {
		migrateDao.migrate(migrateModel);
	}

}
