package com.ipph.migratecore.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.LogDao;
import com.ipph.migratecore.deal.MigrateRowDataHandler;
import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.LogModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.util.MapUtil;

@Service
@Transactional
public class LogService {
	@Resource
	private LogDao logDao;
	/**
	 * 记录日志
	 * @param table
	 * @return
	 */
	public Long log(TableModel table,Map<String,Object> data,Long batchLogId) {
		LogModel log=new LogModel();
		log.setCreateDate(new Date());
		parseData(log, data);
		log.setStatus(LogStatusEnum.FAIL);
		log.setTableId(table.getId());
		log.setTableName(table.getFrom());
		log.setBatchLogId(batchLogId);
		logDao.save(log);
		
		return log.getId();
	}
	/**
	 * 解析获取模型数据
	 * @param log
	 * @param data
	 */
	private void parseData(LogModel log,Map<String,Object> data) {
		
		log.setDataId(Long.parseLong(data.get(MigrateRowDataHandler.pkName).toString()));
		//从集合中去掉主键字段
		data.remove(MigrateRowDataHandler.pkName);
		
		if(data.size()==1) {
			log.setDealData(data.values().iterator().next().toString());
		}else {
			log.setDealData(MapUtil.outMapData(data));
		}
	}
	
	/**
	 * 更新日志
	 * @param logId
	 * @param status
	 * @param message
	 */
	public void updateLog(Long logId,LogStatusEnum status,String message) {
		Optional<LogModel> optionalLog=logDao.findById(logId);//getOne(logId);
				
		LogModel log=optionalLog.get();
		
		if(null!=log) {
			log.setStatus(status);
			log.setMessage(message);
			logDao.save(log);
		}
	}
	/**
	 * 获取表的执行日志
	 * @param batchLogId
	 * @param tableId
	 * @return
	 */
	public List<LogModel> getLogs(Long batchLogId,Long tableId,Pageable pageable) {
		return logDao.getListByBatchLogIdAndTableId(batchLogId,tableId,pageable);
	}
	/**
	 * 查询数据
	 * @param batchLogId
	 * @param tableId
	 * @param status
	 * @param message
	 * @param pageable
	 * @return
	 */
	public List<LogModel> getLogs(Long batchLogId,Long tableId,LogStatusEnum status,String message,Pageable pageable){
		if(null!=status) {
			return logDao.getListByBatchLogIdAndTableIdAndStatus(batchLogId,tableId,status,pageable);
		}
		return logDao.getListByBatchLogIdAndTableId(batchLogId, tableId,pageable);
	}
	
	/**
	 * 判断日志记录是否成功
	 * @param dataId
	 * @param batchLogId
	 * @return
	 */
	public boolean isLogSuccess(Long dataId,Long batchLogId) {
		LogModel log=logDao.getByDataIdAndBatchLogId(dataId,batchLogId);
		if(null!=log&&LogStatusEnum.SUCCESS==log.getStatus()) {
			return true;
		}
		return false;
	}
	/**
	 * 统计信息
	 * @param batchLogId
	 * @param tableId
	 * @return
	 */
	public Map<String,Object> statistic(Long batchLogId,Long tableId){
		
		Map<String,Object> map=null;
		
		List<Map<String,Object>> result=logDao.statistic(batchLogId, tableId);
		if(null!=result&&result.size()>0) {
			map=new HashMap<>();
			for(Map<String,Object> temp:result) {
				map.put(temp.get("status").toString(), temp.get("num"));
			}
		}
		
		return map;
	}
}
