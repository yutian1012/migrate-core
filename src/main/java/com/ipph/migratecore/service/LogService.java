package com.ipph.migratecore.service;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.LogDao;
import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.LogModel;
import com.ipph.migratecore.model.TableModel;

@Service
public class LogService {
	@Resource
	private LogDao logDao;
	/**
	 * 记录日志
	 * @param table
	 * @return
	 */
	public Long log(TableModel table,Map<String,Object> data) {
		LogModel log=new LogModel();
		log.setCreateDate(new Date());
		//log.setDataId(Long.parseLong(pkValue.toString()));
		log.setDealData("");
		log.setStatus(LogStatusEnum.FAIL);
		log.setTableId(table.getId());
		log.setTableName(table.getFrom());
		
		logDao.save(log);
		
		return log.getId();
	}
	/**
	 * 更新日志
	 * @param logId
	 * @param status
	 * @param message
	 */
	public void updateLog(Long logId,LogStatusEnum status,String message) {
		LogModel log=logDao.getOne(logId);
		
		if(null!=log) {
			log.setStatus(status);
			log.setMessage(message);
		}
		
		logDao.save(log);
	}
}
