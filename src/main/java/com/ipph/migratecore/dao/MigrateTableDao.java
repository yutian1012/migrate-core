package com.ipph.migratecore.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.ipph.migratecore.table.MySqlSourceTableMeta;
import com.ipph.migratecore.table.MySqlTargetTableMeta;
import com.ipph.migratecore.table.TableModel;
/**
 * 源表和目标表的结构信息
 */
@Repository
public class MigrateTableDao {
	
	@Resource
	private MySqlSourceTableMeta mySqlSourceTableMeta;
	@Resource
	private MySqlTargetTableMeta mySqlTargetTableMeta;
	
	/**
	 * 获取源数据库的表集合
	 * @return
	 */
	public List<String> getSourceTables(){
		return getTableNameList(true);
	}
	/**
	 * 获取目标数据库的表集合
	 * @return
	 */
	public List<String> getTargetTables(){
		return getTableNameList(false);
	}
	
	private List<String> getTableNameList(boolean isSource){
		try {
			List<TableModel> tableList=null;
			if(isSource) {
				tableList=mySqlSourceTableMeta.getTablesByName(null);
			}else {
				tableList=mySqlTargetTableMeta.getTablesByName(null);
			}
			if(null!=tableList&&tableList.size()>0) {
				List<String> tableNameList=new ArrayList<>(tableList.size());
				for(TableModel table:tableList) {
					if(null!=table)
						tableNameList.add(table.getName());
				}
				return tableNameList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 查询源数据库表信息
	 * @param tableName
	 * @return
	 */
	public TableModel getSourceTable(String tableName) {
		return mySqlSourceTableMeta.getTableByName(tableName);
	}
	/**
	 * 查询目标数据库表信息
	 * @param tableName
	 * @return
	 */
	public TableModel getTargetTable(String tableName) {
		return mySqlTargetTableMeta.getTableByName(tableName);
	}
}
