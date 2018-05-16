package com.ipph.migratecore.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ipph.migratecore.deal.MigrateExceptionHandler;
import com.ipph.migratecore.deal.MigrateRowDataHandler;
import com.ipph.migratecore.deal.exception.ConfigException;
import com.ipph.migratecore.deal.exception.DataNotFoundException;
import com.ipph.migratecore.deal.exception.FormatException;
import com.ipph.migratecore.enumeration.TableOperationEnum;
import com.ipph.migratecore.model.MigrateModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.service.LogService;
import com.ipph.migratecore.service.TableService;
import com.ipph.migratecore.sql.SqlBuilder;
import com.ipph.migratecore.sql.SqlOperation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseMigrateDao {
	protected static final int NUM=50;
	
	@Resource
	protected SqlOperation sqlOperation;
	@Resource
	protected SqlBuilder sqlBuilder;
	
	@Resource
	protected MigrateRowDataHandler migrateRowDataHandler;
	
	@Resource
	protected MigrateExceptionHandler migrateExceptionHandler;
	
	@Resource
	protected  LogService logService;
	@Resource
	private TableService tableService;
	
	public void migrate(MigrateModel migrateModel) {
		
		TableModel table=migrateModel.getTableModel();
		
		//判断是否忽略该表的迁移操作
		if(null==table|| table.isSkip()) return;
		
		/*if(!validateTableModel(migrateModel.getTableModel())) {
			return;
		}*/
		
		//第一步：获取数据源表的select语句--使用该语句实现数据源的查询
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		int size=BaseMigrateDao.NUM;
		
		for(int index=migrateModel.getStart();index<(migrateModel.getSize()+migrateModel.getStart());index+=size){
			if(index+size>=migrateModel.getSize()+migrateModel.getStart()) {
				size=migrateModel.getSize()+migrateModel.getStart()-index;
			}
			String limit=" limit "+index+","+size;
			
			List<Map<String,Object>> result=sqlOperation.getSourceData(select+limit ,migrateRowDataHandler.handleSourceFieldCondition(table));
			//处理查询结果集
			try {
				process(table, result);
			} catch (FormatException | DataNotFoundException e) {
				e.printStackTrace();
			}
			
			result.clear();
		}
		
	}
	
	/**
	 * 校验Table的配置信息
	 * @param table
	 * @throws ConfigException 
	 */
	public boolean validateTableModel(TableModel table) throws ConfigException{
		if(!sqlBuilder.hasPrimaryKey(table)) {
			throw new ConfigException("配置错误，源表中必须存在主键字段！！！");
		}
		if(table.getType()==TableOperationEnum.UPDATE) {
			String updateSql=sqlBuilder.getUpdateSql(table);
			if(null==updateSql||updateSql.indexOf("?")==-1) {
				throw new ConfigException("配置错误，未对更新数据进行条件限定！！！");
			}
		}
		
		return true;
	}
	
	
	//配置切面
	private void process(TableModel table,List<Map<String,Object>> result) throws FormatException, DataNotFoundException {
		
		String executeSql=null;
		
		if(table.getType()==TableOperationEnum.UPDATE) {
			executeSql=sqlBuilder.getUpdateSql(table);
		}
		
		if(null==executeSql) {
			return ;
		}
		
		List<Object[]> batchDataList=processResult(table,result);
		
		//保存数据
		if(batchDataList.size()>0){
			sqlOperation.executeBatchDest(executeSql, batchDataList);
		}
		
		batchDataList.clear();
		
	}
	
	
	private List<Object[]> processResult(TableModel table,List<Map<String,Object>> result) throws FormatException, DataNotFoundException {
		
		List<Object[]> batchDataList=new ArrayList<>(result.size());
		
		if(result!=null&&result.size()>0){
			for(Map<String,Object> row:result){
				Object[] data=processRowData(table, row);
				if(null!=data) {
					batchDataList.add(data);
				}
			}
		}
		
		return batchDataList;
	}
	
	/**
	 * 处理行数据
	 * @param table
	 * @param row
	 * @param targetSelect
	 * @return
	 * @throws FormatException 
	 * @throws DataNotFoundException 
	 */
	private Object[] processRowData(TableModel table,Map<String,Object> row) throws FormatException, DataNotFoundException {
		
		String executeSql=sqlBuilder.getTargetSelectSql(table);
		
		if(null==executeSql) {
			return null;
		}
		
		//格式化数据
		migrateRowDataHandler.handleFormatRowData(table, row);
		
		if(table.getType()==TableOperationEnum.UPDATE){
			//判断待更新的数据是否存在
			if(!sqlOperation.isDestExists(executeSql, migrateRowDataHandler.handleTargetFieldCondtion(row,table))){
				throw new DataNotFoundException("未找到更新记录");
			}
		}
		
		//判断数据是否已经处理过了
		
		
		return migrateRowDataHandler.handleMigrateField(row,table);
	}
	
	/**
	 * 获取待处理的结果集数量
	 * @param table
	 * @return
	 */
	public long getTotal(TableModel table){
		//获取记录数量
		String selectCount=sqlBuilder.getSelectCountSql(table);
		return sqlOperation.getSourceTotal(selectCount, migrateRowDataHandler.handleSourceFieldCondition(table));
	}
	
}
