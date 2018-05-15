package com.ipph.migratecore.service;

import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.MigrateDao;
import com.ipph.migratecore.jms.JmsSender;
import com.ipph.migratecore.model.MigrateMessageModel;
import com.ipph.migratecore.model.TableModel;

@Service
@Transactional
public class MigrateService {
	
	private final static int SIZE=3000;

	@Resource
	private JmsSender jmsSender;
	@Resource
	private MigrateDao migrateDao;
	
	public void migrateTable(TableModel table,Long batchLogId,Long parentLogId){
		
		assert(null!=table);
		
		long total=migrateDao.getTotal(table);
		
		//发送消息实现异步处理
		for(int index=0;index<total;index+=MigrateService.SIZE){
			
			MigrateMessageModel messageModel=new MigrateMessageModel();
			messageModel.setType(table.getType());
			messageModel.setTableId(table.getId());
			messageModel.setBatchLogId(batchLogId);
			messageModel.setParentLogId(parentLogId);
			messageModel.setTotal(total);
			messageModel.setStart(index);
			messageModel.setSize(MigrateService.SIZE);
			
			jmsSender.sendMessageModelByQueue(messageModel);
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
