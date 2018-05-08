package com.ipph.migratecore.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipph.migratecore.model.BatchTableModel;

public interface BatchTableDao extends JpaRepository<BatchTableModel, Long>{

	public List<BatchTableModel> getListByBatchId(Long batchId);
}
