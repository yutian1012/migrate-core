package com.ipph.migratecore.deal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ipph.migratecore.deal.exception.DataExistsException;
import com.ipph.migratecore.deal.exception.DataNotFoundException;
import com.ipph.migratecore.deal.exception.FormatException;
import com.ipph.migratecore.model.TableModel;

@Component
public class MigrateExceptionHandler {
	/*@Resource
	private JdbcTemplate errorJdbcTemplate;*/
	/*@Resource
	private RowDataHandler rowDataHandler;
	@Resource
	private SqlBuilder sqlBuilder;*/
	
	private ConcurrentMap<String, Set<Long>> concurrentMap=new ConcurrentHashMap<>();
	
	/*private Logger log=Logger.getLogger(MigrateExceptionHandler.class);*/
	/**
	 * 处理消息格式化异常
	 * @param e
	 * @param sql
	 * @param row
	 * @param table
	 */
	public void formatExceptionHandler(FormatException e,Map<String,Object> row,TableModel table,boolean isUpdate){
		/*log.error("格式化数据错误"+e.getMessage());
		log.error("数据对象"+MapUtil.outMapData(row));
		insertErrorInof(row, table, isUpdate);*/
	}
	
	/**
	 * 更新数据时未找到记录
	 * @param e
	 * @param sql
	 * @param row
	 * @param table
	 */
	public void dataNotFoundExceptionHandler(DataNotFoundException e,Map<String,Object> row,TableModel table){
		/*log.error(e.getMessage());
		log.error("数据对象"+MapUtil.outMapData(row));
		insertErrorInof(row, table, false);*/
	}
	/**
	 * 处理异常错误数据
	 * @param e
	 * @param sql
	 * @param row
	 * @param table
	 */
	public void sqlExceptionHandler(Exception e,Map<String,Object> row,TableModel table,boolean isUpdate){
		/*log.error("语句执行错误"+e.getMessage());
		log.error("数据对象"+MapUtil.outMapData(row));
		insertErrorInof(row, table, isUpdate);*/
	}
	/**
	 * 处理错误数据
	 * @param e
	 * @param row
	 * @param table
	 */
	public void dataExistsExceptionHandler(DataExistsException e,Map<String, Object> row, TableModel table) {
		/*log.error(e.getMessage());
		log.error("数据对象"+MapUtil.outMapData(row));
		
		insertErrorInof(row, table, false);*/
		
	}
	
	private void insertErrorInof(Map<String,Object> row,TableModel table,boolean isUpdate){
		if(isUpdate){
			return;
		}
	}
	
	private synchronized boolean put(String tableName,Long value){
		if(!concurrentMap.containsKey(tableName)){
			concurrentMap.put(tableName, new HashSet<Long>());
		}
		return concurrentMap.get(tableName).add(value);
	}
}
