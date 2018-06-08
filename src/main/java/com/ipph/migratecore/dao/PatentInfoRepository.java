package com.ipph.migratecore.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ipph.migratecore.enumeration.LogMessageEnum;
import com.ipph.migratecore.model.PatentInfoCheck;

public interface PatentInfoRepository extends JpaRepository<PatentInfoCheck,Long> {
	
	public List<PatentInfoCheck> findByBatchLogId(Long batchLogId,Pageable pageable);
	
	public List<PatentInfoCheck> findByBatchLogIdAndTypeAndCostTypeIn(Long batchLogId,LogMessageEnum type,String[] costType,Pageable page);

}
