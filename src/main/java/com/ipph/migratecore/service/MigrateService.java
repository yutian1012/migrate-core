package com.ipph.migratecore.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.MigrateDao;
import com.ipph.migratecore.deal.exception.ConfigException;
import com.ipph.migratecore.jms.JmsSender;
import com.ipph.migratecore.model.MigrateModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.thread.MigrateTask;
import com.ipph.migratecore.thread.ThreadPool;

@Service
public class MigrateService {
	
	private final static int SIZE=3000;

	@Resource
	private JmsSender jmsSender;
	@Resource
	private MigrateDao migrateDao;
	@Resource
	private ThreadPool ThreadPool;
	
	public void migrateTable(TableModel table,Long batchLogId,Long parentLogId) throws ConfigException{
		
		assert(null!=table);
		//校验
		if(!migrateDao.validateTableModel(table)) {
			return;
		}
		
		long total=migrateDao.getTotal(table);
		
		//发送消息实现异步处理
		for(int index=0;index<total;index+=MigrateService.SIZE){
			
			MigrateModel migrateModel=new MigrateModel();
			migrateModel.setType(table.getType());
			migrateModel.setTableModel(table);
			migrateModel.setBatchLogId(batchLogId);
			migrateModel.setParentLogId(parentLogId);
			migrateModel.setTotal(total);
			migrateModel.setStart(index);
			migrateModel.setSize(MigrateService.SIZE);
			
			//jmsSender.sendmigrateModelByQueue(migrateModel);

			handleMessagModel(migrateModel);
		}
	}
	
	private void handleMessagModel(MigrateModel migrateModel) {
    	
    	if(null!=migrateModel.getTableModel()) {
    		
    		//MigrationTask task=new MigrationTask(migrateDao, migrateModel.getTableModel(), migrateModel.getBatchLogId(), migrateModel.getParentLogId(), migrateModel.getStart(), migrateModel.getSize());
    		MigrateTask task=new MigrateTask(migrateDao,migrateModel);
    		
    		ThreadPool.addTask(task);
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
