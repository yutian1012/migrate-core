package com.ipph.migratecore.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipph.migratecore.model.BatchLogModel;

public interface BatchLogDao extends JpaRepository<BatchLogModel,Long>{
	public List<BatchLogModel> getListByBatchId(Long batchId);
	
	public List<BatchLogModel> getBatchLogByBatchIdAndParentIdIsNull(Long batchId);
	
	public List<BatchLogModel> getListByParentId(Long parentId);
	
	public BatchLogModel findFirstByParentIdOrderByIdDesc(Long parentId);
}
