package com.ipph.migratecore.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.LogDao;
import com.ipph.migratecore.dao.LogJdbcDao;
import com.ipph.migratecore.deal.MigrateRowDataHandler;
import com.ipph.migratecore.enumeration.LogMessageEnum;
import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.BatchLogModel;
import com.ipph.migratecore.model.LogModel;
import com.ipph.migratecore.model.MigrateModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.patent.util.DateFormatUtil;
import com.ipph.migratecore.util.MapUtil;

@Service
public class LogService {
	@Resource
	private LogDao logDao;
	@Resource
	private LogJdbcDao logJdbcDao;
	@Resource
	private BatchLogService batchLogService;
	
	private static Logger log = LoggerFactory.getLogger(LogService.class);
	
	/**
	 * 更新日志
	 * @param logId
	 * @param status
	 * @param message
	 */
	public void updateLog(Long logId,LogStatusEnum status,String message,LogMessageEnum messageType) {
		LogModel log=logDao.findById(logId).get();
		
		if(null!=log) {
			log.setStatus(status);
			log.setMessage(message);
			log.setMessageType(messageType);
			//logJdbcDao.save(log);
			logDao.save(log);
		}
	}
	
	/**
	 * 查询批次中某表的执行日志
	 * @param batchLogId
	 * @param tableId
	 * @param status
	 * @param message
	 * @param pageable
	 * @return
	 */
	public List<LogModel> getLogs(Long batchLogId,Long tableId,LogStatusEnum status,LogMessageEnum messageType,Pageable pageable){
		if(null!=status) {
			return logDao.getListByBatchLogIdAndTableIdAndStatus(batchLogId, tableId, status, pageable);
		}else if(null!=messageType){
			return logDao.getListByBatchLogIdAndTableIdAndMessageType(batchLogId, tableId, messageType, pageable);
		}
		return logDao.getListByBatchLogIdAndTableId(batchLogId, tableId, pageable);
	}
	/**
	 * 获取批次执行的数量
	 * @param batchLogId
	 * @return
	 */
	public long countByBatchLogId(Long batchLogId) {
		return logDao.countByBatchLogId(batchLogId);
	}
	
	/**
	 * 获取批次处理的正确数据（包括子批次的处理数据）
	 * @param batchLogId
	 * @param tableId
	 * @param pageable
	 * @return
	 */
	public List<LogModel> getSuccessLogs(Long batchLogId,Long tableId,Pageable pageable){
		
		Long[] subBatchLogIdArr=batchLogService.getSubBatchLogIdArr(batchLogId);
		
		List<LogModel> list=null;
		
		Long[] batchLogIdArr=new Long[null!=subBatchLogIdArr?subBatchLogIdArr.length+1:1];
		
		batchLogIdArr[0]=batchLogId;
		
		//获取所有该批次日志中的所有执行记录（包括该批次的日志已经该批次的子批次日志）
		if(null!=subBatchLogIdArr&&subBatchLogIdArr.length>0) {
			System.arraycopy(subBatchLogIdArr, 0, batchLogIdArr, 1, subBatchLogIdArr.length);
		}
		
		list=logDao.getListByTableIdAndStatusAndBatchLogIdIn(tableId,LogStatusEnum.SUCCESS,batchLogIdArr,pageable);
		
		return list;
	}
	
	/**
	 * 获取批次处理的错误数据
	 * （批次是迭代执行的，因此错误记录只需要查看最后一次子批次错误记录即可，如果没有子批次日志，则查看该批次的执行日志）
	 * @param batchLogId
	 * @param tableId
	 * @param pageable
	 * @return
	 */
	public List<LogModel> getFailLogs(Long parentBatchLogId,Long tableId,Pageable pageable){
		//获取最新的子批次
		BatchLogModel subBatchLogModel=batchLogService.findFirstByParentIdOrderByIdDesc(parentBatchLogId);
		Long batchLogId=parentBatchLogId;
		if(null!=subBatchLogModel) {
			batchLogId=subBatchLogModel.getId();
		}
		return logDao.getListByBatchLogIdAndTableIdAndStatus(batchLogId, tableId,LogStatusEnum.FAIL,pageable);
	}
	
	public List<LogModel> getPatentFailLogs(Long parentBatchLogId,Long tableId,Pageable pageable){
		BatchLogModel subBatchLogModel=batchLogService.findFirstByParentIdOrderByIdDesc(parentBatchLogId);
		Long batchLogId=parentBatchLogId;
		if(null!=subBatchLogModel) {
			batchLogId=subBatchLogModel.getId();
		}
		return logDao.getListByBatchLogIdAndTableIdAndStatusWithPatent(batchLogId, tableId,LogStatusEnum.FAIL,pageable);
	}
	
	
	/**
	 * 判断日志记录是否成功
	 * @param dataId
	 * @param batchLogId
	 * @return
	 */
	public boolean isLogSuccess(Long dataId,Long batchLogId) {
		//LogModel log=logJdbcDao.getByDataIdAndBatchLogId(dataId,batchLogId);
		LogModel log=logDao.getByDataIdAndBatchLogId(dataId,batchLogId);
		if(null!=log&&LogStatusEnum.SUCCESS==log.getStatus()) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 批量保存
	 * @param list
	 */
	public void insert(List<LogModel> list) {
		logJdbcDao.saveAll(list);
	}
	
	
	
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
		//logJdbcDao.save(log);
		logDao.save(log);
		
		return log.getId();
	}
	/**
	 * 记录日志
	 * @param migrateModel
	 * @param messageType
	 * @param data
	 */
	public void addLog(MigrateModel migrateModel,LogMessageEnum messageType,Map<String,Object> data,String exception) {
		LogModel log=getLogModel(migrateModel, messageType, data);
		log.setException(exception);
		//logJdbcDao.save(log);
		logDao.save(log);
	}
	/**
	 * 获取日志
	 * @param migrateModel
	 * @param messageType
	 * @param data
	 * @return
	 */
	public LogModel getLogModel(MigrateModel migrateModel,LogMessageEnum messageType,Map<String,Object> data) {
		
		if(null==messageType) {
			messageType=LogMessageEnum.SUCCESS;
		}
		
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
		
		if(null!=data.get(MigrateRowDataHandler.pkName)) {
			log.setDataId(Long.parseLong(data.get(MigrateRowDataHandler.pkName).toString()));
			//从集合中去掉主键字段
			data.remove(MigrateRowDataHandler.pkName);
		}else if(null!=data.get(MigrateRowDataHandler.fkName)){
			log.setDataId(Long.parseLong(data.get(MigrateRowDataHandler.fkName).toString()));
			data.remove(MigrateRowDataHandler.fkName);
		}
		
		if(data.size()==1) {
			Object value=data.values().iterator().next();
			log.setDealData(null!=value?value.toString():null);
		}else {
			log.setDealData(MapUtil.outMapData(data));
		}
	}
	/**
	 * 获取日志集合
	 * @param batchLogId
	 * @param type
	 * @return
	 */
	public List<Object> getDealDataListByBatchLogIdAndMessageType(Long batchLogId,LogMessageEnum messageType,Pageable pageable){
		return logDao.getDealDataListByBatchLogIdAndMessageType(batchLogId,messageType,pageable);
	}
	
	/**
	 * 统计数据量
	 * @param batchLogId
	 * @param messageType
	 * @return
	 */
	public Long countByBatchLogIdAndMessageType(Long batchLogId,LogMessageEnum messageType) {
		return logDao.countByBatchLogIdAndMessageType(batchLogId,messageType);
	}
	
	public List<LogModel> getListByBatchLogIdAndMessageType(Long batchLogId,LogMessageEnum messageType,Pageable pageable){
		return logDao.getListByBatchLogIdAndMessageType(batchLogId,messageType,pageable);
	}
	
	/**
	 * 记录执行sql
	 * @param sql
	 * @param batchDataList
	 */
	public void logSql(String sql,List<Object[]> batchDataList) {
		for(Object[] data:batchDataList) {
			logSql(sql, data);
		}
	}
	
	public void logSql(String sql,Object[] data) {
		String sqltmp=sql;
		try {
			sqltmp=sqltmp.replaceAll("[?]", "%%");
			for(int i=0;i<data.length;i++) {
				if(null==data[i]) {//处理控制
					sqltmp=sqltmp.replaceFirst("%%",data[i]+"");
				}else if(data[i] instanceof Date) {
					sqltmp=sqltmp.replaceFirst("%%", "'"+DateFormatUtil.format((Date)data[i], "yyyy-MM-dd")+"'");
				}else {
					//特殊符号的过滤
					sqltmp=sqltmp.replaceFirst("%%","'"+data[i]+"'");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(log.isInfoEnabled()) {
			log.info(sqltmp+";");
		}
	}
}
