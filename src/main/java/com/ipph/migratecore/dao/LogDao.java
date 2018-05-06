package com.ipph.migratecore.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipph.migratecore.model.LogModel;

public interface LogDao extends JpaRepository<LogModel,Long>{

}
