package com.ipph.migratecore.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.LogModel;

public interface ILogDao {
	
	public List<LogModel> getListByBatchLogIdAndTableIdAndStatusWithPatent(Long batchLogId,Long tableId,LogStatusEnum status,Pageable pageable);
}
