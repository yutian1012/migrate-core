package com.ipph.migratecore.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.LogDao;
import com.ipph.migratecore.dao.LogJdbcDao;
import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.BatchLogModel;

@Service
public class LogStatisticService {
	@Resource
	private LogDao logDao;
	@Resource
	private LogJdbcDao logJdbcDao;
	@Resource
	private BatchLogService BatchLogService;
	
	/**
	 * 统计信息
	 * @param batchLogId
	 * @param tableId
	 * @return
	 */
	public Map<String,Object> statistic(Long batchLogId,Long tableId){
		
		List<Map<String,Object>> result=logDao.statistic(batchLogId, tableId);//logJdbcDao.statistic(batchLogId, tableId);
		
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
		BatchLogModel subBatchLogModel=BatchLogService.findFirstByParentIdOrderByIdDesc(batchLogId);
		Long batchLogIdForSearch=batchLogId;
		if(null!=subBatchLogModel) {
			batchLogIdForSearch=subBatchLogModel.getId();
		}
		
		List<Map<String,Object>> result=logDao.statisticByStatus(batchLogIdForSearch, tableId, LogStatusEnum.FAIL);//logJdbcDao.statisticError(batchLogIdForSearch, tableId);
		
		return processStatisticResult(result);
	}
	
	/**
	 * 获取子批次的执行结果
	 * @param parentBatchLogId
	 * @param tableId
	 * @return
	 */
	public Map<String,Object> statisticByParentBatchLog(Long parentBatchLogId,Long tableId){
		
		Long[] batchLogIdArr=BatchLogService.getSubBatchLogIdArr(parentBatchLogId);
		
		if(null!=batchLogIdArr) {
			List<Map<String,Object>> result=logDao.statisticBybatchLogIdIn(batchLogIdArr);//logJdbcDao.getstatisticBybatchLogIdIn(batchLogIdArr);
			
			return processStatisticResult(result);
		}
		
		return null;
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
}
