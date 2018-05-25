package com.ipph.migratecore.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ipph.migratecore.enumeration.LogMessageEnum;
import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.LogModel;

public interface LogDao extends JpaRepository<LogModel,Long>,ILogDao{

	public List<LogModel> getListByBatchLogIdAndTableId(Long batchLogId,Long tableId);
	
	public List<LogModel> getListByBatchLogIdAndTableId(Long batchLogId,Long tableId,Pageable pageable);
	
	public List<LogModel> getListByBatchLogIdAndTableIdAndStatus(Long batchLogId,Long tableId,LogStatusEnum status,Pageable pageable);
	
	/*@Query("select log from LogModel as log inner join PatentInfo as patent where log.dealData=patent.appNumber and log.batchLogId=?1 and log.tableId=?2 and log.status=?3")
	public List<LogModel> getListByBatchLogIdAndTableIdAndStatusWithPatent(Long batchLogId,Long tableId,LogStatusEnum status,Pageable pageable);*/
	
	public List<LogModel> getListByBatchLogIdAndTableIdAndMessageType(Long batchLogId,Long tableId,LogMessageEnum messageType,Pageable pageable);
	
	public List<LogModel> getListByBatchLogIdAndMessageType(Long batchLogId,LogMessageEnum messageType,Pageable pageable);
	
	public Long countByBatchLogIdAndMessageType(Long batchLogId,LogMessageEnum messageType);
	
	public LogModel getByDataIdAndBatchLogId(Long dataId,Long batchLogId);
	
	@Query(" select count(1) as num ,log.status as category from LogModel log where log.batchLogId=?1 and log.tableId=?2 group by log.status ")
	public List<Map<String,Object>> statistic(Long batchLogId,Long tableId);
	
	@Query(" select count(1) as num ,log.messageType as category from LogModel log where log.batchLogId=?1 and log.tableId=?2 and log.status=?3 group by log.status ")
	public List<Map<String,Object>> statisticByStatus(Long batchLogId,Long tableId,LogStatusEnum status);
	
	@Query("select count(1) as num,log.status as category from LogModel log where log.batchLogId in (?1) group by log.status")
	public List<Map<String,Object>> statisticBybatchLogIdIn(Long[] batchLogId);
	
	public List<LogModel> getListByTableIdAndStatusAndBatchLogIdIn(Long tableId,LogStatusEnum status,Long[] batchLogIdArr,Pageable pageable);
	
	
}
