package com.ipph.migratecore.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.BatchLogDao;
import com.ipph.migratecore.dao.LogJdbcDao;
import com.ipph.migratecore.deal.MigrateRowDataHandler;
import com.ipph.migratecore.enumeration.LogMessageEnum;
import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.BatchLogModel;
import com.ipph.migratecore.model.LogModel;
import com.ipph.migratecore.model.MigrateModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.util.MapUtil;

@Service
public class LogService {
	@Resource
	//private LogDao logDao;
	private LogJdbcDao logJdbcDao;
	@Resource
	private BatchLogDao batchLogDao;
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
		logJdbcDao.save(log);
		
		return log.getId();
	}
	
	public LogModel getLogModel(MigrateModel migrateModel,LogMessageEnum messageType,Map<String,Object> data) {
		
		LogStatusEnum status=messageType==LogMessageEnum.SUCCESS?LogStatusEnum.SUCCESS:LogStatusEnum.FAIL;
		
		return getLogModel(status, messageType, messageType.getValue(), migrateModel.getTableModel(), migrateModel.getBatchLogId(), data);
		
	}
	
	/**
	 * 获取数据
	 * @param status
	 * @param table
	 * @param batchLogId
	 * @param data
	 * @return
	 */
	public LogModel getLogModel(LogStatusEnum status,LogMessageEnum messageType,String message,TableModel table,Long batchLogId,Map<String,Object> data) {
		LogModel log=new LogModel();
		log.setCreateDate(new Date());
		log.setStatus(status);
		log.setMessageType(messageType);
		log.setTableId(table.getId());
		log.setTableName(table.getFrom());
		log.setBatchLogId(batchLogId);
		log.setMessage(message);
		parseData(log, data);
		return log;
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
	public void updateLog(Long logId,LogStatusEnum status,String message,LogMessageEnum messageType) {
		//Optional<LogModel> optionalLog=logDao.findById(logId);//getOne(logId);
		//LogModel log=optionalLog.get();
		
		LogModel log=logJdbcDao.findById(logId);
		
		if(null!=log) {
			log.setStatus(status);
			log.setMessage(message);
			log.setMessageType(messageType);
			logJdbcDao.save(log);
		}
	}
	
	/**
	 * 获取表的执行日志
	 * @param batchLogId
	 * @param tableId
	 * @return
	 */
	public List<LogModel> getLogs(Long batchLogId,Long tableId) {
		return logJdbcDao.getListByBatchLogIdAndTableId(batchLogId,tableId);
	}
	/**
	 * 获取表的执行日志
	 * @param batchLogId
	 * @param tableId
	 * @return
	 */
	public List<LogModel> getLogs(Long batchLogId,Long tableId,Pageable pageable) {
		return logJdbcDao.getListByBatchLogIdAndTableId(batchLogId,tableId,pageable);
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
	public List<LogModel> getLogs(Long batchLogId,Long tableId,LogStatusEnum status,LogMessageEnum messageType,Pageable pageable){
		if(null!=status) {
			return logJdbcDao.getListByBatchLogIdAndTableIdAndStatus(batchLogId,tableId,status,pageable);
		}else if(null!=messageType){
			return logJdbcDao.getListByBatchLogIdAndTableIdAndMessageType(batchLogId, tableId, messageType, pageable);
		}
		return logJdbcDao.getListByBatchLogIdAndTableId(batchLogId, tableId,pageable);
	}
	/**
	 * 获取批次处理的正确数据（包括子批次的处理数据）
	 * @param batchLogId
	 * @param tableId
	 * @param pageable
	 * @return
	 */
	public List<LogModel> getSuccessLogs(Long batchLogId,Long tableId,Pageable pageable){
		Long[] subBatchLogIdArr=getSubBatchLogIdArr(batchLogId);
		
		List<LogModel> list=null;
		if(null!=subBatchLogIdArr&&subBatchLogIdArr.length>0) {
			Long[] batchLogIdArr=new Long[subBatchLogIdArr.length+1];
			batchLogIdArr[0]=batchLogId;
			System.arraycopy(subBatchLogIdArr, 0, batchLogIdArr, 1, subBatchLogIdArr.length);
			list=logJdbcDao.getSuccessListByTableIdAndBatchLogIdIn(tableId,batchLogIdArr, pageable);
		}
		
		return list;
	}
	
	/**
	 * 获取批次处理的正确数据（包括子批次的处理数据）
	 * @param batchLogId
	 * @param tableId
	 * @param pageable
	 * @return
	 */
	public List<LogModel> getFailLogs(Long parentBatchLogId,Long tableId,Pageable pageable){
		//获取最新的子批次
		BatchLogModel subBatchLogModel=batchLogDao.findFirstByParentIdOrderByIdDesc(parentBatchLogId);
		Long batchLogId=parentBatchLogId;
		if(null!=subBatchLogModel) {
			batchLogId=subBatchLogModel.getId();
		}
		return logJdbcDao.getFailListByBatchLogIdAndTableId(batchLogId, tableId,pageable);
	}
	
	
	/**
	 * 判断日志记录是否成功
	 * @param dataId
	 * @param batchLogId
	 * @return
	 */
	public boolean isLogSuccess(Long dataId,Long batchLogId) {
		LogModel log=logJdbcDao.getByDataIdAndBatchLogId(dataId,batchLogId);
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
		
		List<Map<String,Object>> result=logJdbcDao.statistic(batchLogId, tableId);
		
		return processStatisticResult(result);
		
	}
	/**
	 * 获取错误批次统计信息
	 * @param batchLogId
	 * @param tableId
	 * @return
	 */
	public Map<String,Object> errorstatistic(Long batchLogId,Long tableId){
		//获取最新的子批次
		BatchLogModel subBatchLogModel=batchLogDao.findFirstByParentIdOrderByIdDesc(batchLogId);
		Long batchLogIdForSearch=batchLogId;
		if(null!=subBatchLogModel) {
			batchLogIdForSearch=subBatchLogModel.getId();
		}
		
		List<Map<String,Object>> result=logJdbcDao.statisticError(batchLogIdForSearch, tableId);
		
		return processStatisticResult(result);
	}
	
	/**
	 * 获取子批次的执行结果
	 * @param parentBatchLogId
	 * @param tableId
	 * @return
	 */
	public Map<String,Object> statisticByParentBatchLog(Long parentBatchLogId,Long tableId){
		
		Long[] batchLogIdArr=getSubBatchLogIdArr(parentBatchLogId);
		
		if(null!=batchLogIdArr) {
			List<Map<String,Object>> result=logJdbcDao.getstatisticBybatchLogIdIn(batchLogIdArr);//logJdbcDao.statisticByParentBatchLog(parentBatchLogId, tableId);
			
			return processStatisticResult(result);
		}
		
		return null;
	}
	/**
	 * 获取子批次主键数组集合
	 * @param parentBatchLogId
	 * @return
	 */
	private Long[] getSubBatchLogIdArr(Long parentBatchLogId) {
		//获取对应的子批次列表
		List<BatchLogModel> batchLogList=batchLogDao.getListByParentId(parentBatchLogId);
		
		Long[] batchLogIdArr=null;
		if(null!=batchLogList&&batchLogList.size()>0) {
			batchLogIdArr=new Long[batchLogList.size()];
			
			int i=0;
			for(BatchLogModel batchLog:batchLogList) {
				batchLogIdArr[i++]=batchLog.getId();
			}
		}
		return batchLogIdArr;
	}
	
	/**
	 * 处理统计数据
	 * @param result
	 * @return
	 */
	private Map<String,Object> processStatisticResult(List<Map<String,Object>> result) {
		
		Map<String,Object> map=null;
		
		if(null!=result&&result.size()>0) {
			map=new HashMap<>();
			for(Map<String,Object> temp:result) {
				map.put(temp.get("category").toString(), temp.get("num"));
			}
		}
		
		return map;
	}
	/**
	 * 批量保存
	 * @param list
	 */
	public void insert(List<LogModel> list) {
		logJdbcDao.saveAll(list);
	}
}
