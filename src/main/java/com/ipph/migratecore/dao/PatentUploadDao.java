package com.ipph.migratecore.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipph.migratecore.model.PatentUploadModel;

public interface PatentUploadDao extends JpaRepository<PatentUploadModel,Long>{

}
