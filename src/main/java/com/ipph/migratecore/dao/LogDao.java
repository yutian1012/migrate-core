package com.ipph.migratecore.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipph.migratecore.model.LogModel;

public interface LogDao extends JpaRepository<LogModel,Long>{

	public List<LogModel> getListByBatchLogIdAndTableId(Long batchLogId,Long tableId);
	
	public LogModel getByDataIdAndBatchLogId(Long dataId,Long batchLogId);
}
