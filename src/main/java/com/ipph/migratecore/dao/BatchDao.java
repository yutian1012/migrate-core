package com.ipph.migratecore.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipph.migratecore.model.BatchModel;

public interface BatchDao extends JpaRepository<BatchModel, Long>{
	
}
