package com.ipph.migratecore.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipph.migratecore.model.TableModel;

public interface TableDao extends JpaRepository<TableModel, Long>{

}
