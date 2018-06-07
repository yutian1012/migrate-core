package com.ipph.migratecore.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipph.migratecore.model.PatentInfo;

public interface PatentDao extends JpaRepository<PatentInfo, Long>{
	
	public boolean existsByAppNumber(String appNumber);
	
	public PatentInfo findByAppNumber(String appNumber);
}
