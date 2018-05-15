package com.ipph.migratecore.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.LogModel;

public interface LogDao extends JpaRepository<LogModel,Long>{

	public List<LogModel> getListByBatchLogIdAndTableId(Long batchLogId,Long tableId);
	
	public List<LogModel> getListByBatchLogIdAndTableId(Long batchLogId,Long tableId,Pageable pageable);
	
	public List<LogModel> getListByBatchLogIdAndTableIdAndStatus(Long batchLogId,Long tableId,LogStatusEnum status,Pageable pageable);
	
	public List<LogModel> getListByBatchLogIdAndTableIdAndMessage(Long batchLogId,Long tableId,String message,Pageable pageable);
	
	public LogModel getByDataIdAndBatchLogId(Long dataId,Long batchLogId);
	
	@Query(" select count(1) as num ,log.status as status from LogModel log where log.batchLogId=?1 and log.tableId=?2 group by log.status ")
	public List<Map<String,Object>> statistic(Long batchLogId,Long tableId);
}
