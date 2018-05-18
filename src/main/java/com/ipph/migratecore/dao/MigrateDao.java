package com.ipph.migratecore.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.ipph.migratecore.deal.MigrateExceptionHandler;
import com.ipph.migratecore.deal.MigrateRowDataHandler;
import com.ipph.migratecore.deal.exception.ConfigException;
import com.ipph.migratecore.deal.exception.DataExistsException;
import com.ipph.migratecore.deal.exception.DataNotFoundException;
import com.ipph.migratecore.deal.exception.FormatException;
import com.ipph.migratecore.enumeration.LogMessageEnum;
import com.ipph.migratecore.enumeration.TableOperationEnum;
import com.ipph.migratecore.model.LogModel;
import com.ipph.migratecore.model.MigrateModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.service.LogService;
import com.ipph.migratecore.service.TableService;
import com.ipph.migratecore.sql.SqlBuilder;
import com.ipph.migratecore.sql.SqlOperation;

@Repository
public class MigrateDao {
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
		
		//第一步：获取数据源表的select语句--使用该语句实现数据源的查询
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		int size=MigrateDao.NUM;
		
		for(int index=migrateModel.getStart();index<(migrateModel.getSize()+migrateModel.getStart());index+=size){
			if(index+size>=migrateModel.getSize()+migrateModel.getStart()) {
				size=migrateModel.getSize()+migrateModel.getStart()-index;
			}
			String limit=" limit "+index+","+size;
			
			List<Map<String,Object>> result=sqlOperation.getSourceData(select+limit ,migrateRowDataHandler.handleSourceFieldCondition(table));
			//处理查询结果集
			if(null!=result&&result.size()>0) {
				
				process(migrateModel, result);
				
				result.clear();
			}
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
	
	/**
	 * 执行数据迁移操作
	 * @param migrateModel
	 * @param result
	 */
	private void process(MigrateModel migrateModel,List<Map<String,Object>> result) {
		
		String executeSql=null;
		
		TableModel table=migrateModel.getTableModel();
		
		if(table.getType()==TableOperationEnum.UPDATE) {
			executeSql=sqlBuilder.getUpdateSql(table);
		}else {
			executeSql=sqlBuilder.getInsertSql(table);
		}
		
		if(null==executeSql) {
			return ;
		}
		
		List<Object[]> batchDataList=new ArrayList<>(result.size());
		List<LogModel> logModelList=processResult(migrateModel, result, batchDataList);
		
		//保存数据
		if(batchDataList.size()>0){
			sqlOperation.executeBatchDest(executeSql, batchDataList);
			batchDataList.clear();
		}
		
		//保存日志
		if(null!=logModelList&&logModelList.size()>0) {
			logService.insert(logModelList);
			logModelList.clear();
		}
		
	}
	
	/**
	 * 处理结果集
	 * @param table
	 * @param result
	 * @return
	 * @throws FormatException
	 * @throws DataNotFoundException
	 */
	private List<LogModel> processResult(MigrateModel migrateModel,List<Map<String,Object>> result,List<Object[]> batchDataList) {
		
		List<LogModel> logModelList=new ArrayList<>();
		
		if(result!=null&&result.size()>0){
			for(Map<String,Object> row:result){
				
				Object[] data=null;
				LogMessageEnum messageType=LogMessageEnum.SUCCESS;
				
				try {
					data = processRowData(migrateModel, row);
				} catch (FormatException | DataNotFoundException | DataExistsException e) {
					messageType=migrateExceptionHandler.handle(e);
				}
				
				if(null!=data) {
					batchDataList.add(data);
				}
				if(data!=null||messageType!=LogMessageEnum.SUCCESS) {
					logModelList.add(logService.getLogModel(migrateModel,messageType,migrateRowDataHandler.handleForLog(migrateModel.getTableModel(),row)));
				}
			}
		}
		
		return logModelList;
	}
	
	/**
	 * 处理行数据
	 * @param table
	 * @param row
	 * @param targetSelect
	 * @return
	 * @throws FormatException 
	 * @throws DataNotFoundException 
	 * @throws DataExistsException 
	 */
	private Object[] processRowData(MigrateModel migrateModel,Map<String,Object> row) throws FormatException, DataNotFoundException, DataExistsException {
		
		TableModel table=migrateModel.getTableModel();
		
		String targetSelectSql=sqlBuilder.getTargetSelectSql(table);
		
		if(null==targetSelectSql) {
			return null;
		}
		
		//格式化数据
		migrateRowDataHandler.handleFormatRowData(table, row);
		
		if(table.getType()==TableOperationEnum.UPDATE){
			//判断待更新的数据是否存在
			if(!sqlOperation.isDestExists(targetSelectSql, migrateRowDataHandler.handleTargetFieldCondtion(row,table))){
				throw new DataNotFoundException("未找到更新记录");
			}
		}else {
			if(sqlOperation.isDestExists(targetSelectSql, migrateRowDataHandler.handleTargetConstraintCondtion(row,table))){
				throw new DataExistsException("待插入的记录已经存在");
			}
		}
		
		//判断数据是否已经处理过了
		if(null!=migrateModel.getParentLogId()&&logService.isLogSuccess((Long)row.get(table.getSourcePkName()),migrateModel.getParentLogId())) {
			return null;
		}
		
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
	
	/**
	 * 获取记录信息
	 * @param table
	 * @param dataId
	 * @return
	 */
	public Map<String,Object> getRecord(TableModel table,Object dataId){
		String sql=sqlBuilder.getAllFieldSelectWithPK(table);
		
		List<Map<String,Object>> result=sqlOperation.getSourceData(sql, new Object[] {dataId});
		
		if(null!=result&&result.size()>0) {
			return result.get(0);
		}
		return null;
	}
	
}
