package com.ipph.migratecore.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.ipph.migratecore.deal.MigrateExceptionHandler;
import com.ipph.migratecore.deal.MigrateRowDataHandler;
import com.ipph.migratecore.deal.exception.ConfigException;
import com.ipph.migratecore.deal.exception.DataAlreadyDealedException;
import com.ipph.migratecore.deal.exception.DataExistsException;
import com.ipph.migratecore.deal.exception.DataNotFoundException;
import com.ipph.migratecore.deal.exception.FormatException;
import com.ipph.migratecore.deal.exception.SplitException;
import com.ipph.migratecore.enumeration.LogMessageEnum;
import com.ipph.migratecore.enumeration.TableOperationEnum;
import com.ipph.migratecore.model.LogModel;
import com.ipph.migratecore.model.MigrateModel;
import com.ipph.migratecore.model.SplitModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.service.LogService;
import com.ipph.migratecore.service.TableService;
import com.ipph.migratecore.sql.SqlBuilder;
import com.ipph.migratecore.sql.SqlOperation;

@Repository
public class MigrateDao {
	protected static final int NUM=1;//50;
	
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
	private void process(MigrateModel migrateModel,List<Map<String,Object>> result){
		
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
		try {
			batchProcess(migrateModel, result, executeSql);
		}catch (RuntimeException e) {
			singleProcess(migrateModel, result, executeSql);
		}
	}
	/**
	 * 单条执行记录(对批次操作错误的情况下进行逐条单独执行)
	 * @param migrateModel
	 * @param result
	 * @param executeSql
	 */
	private void singleProcess(MigrateModel migrateModel,List<Map<String,Object>> result,String executeSql) {
		if(result!=null&&result.size()>0){
			for(Map<String,Object> row:result){
				
				Object[] data=null;
				String exception="";
				LogMessageEnum messageType=LogMessageEnum.SUCCESS;
				
				try {
					data = processRowData(migrateModel, row);
					sqlOperation.executeDest(executeSql, data);
					
					logService.logSql(executeSql, data);
					
				} catch (FormatException | DataNotFoundException | DataExistsException |RuntimeException | DataAlreadyDealedException e) {
					messageType=migrateExceptionHandler.handle(e);
					if(messageType==LogMessageEnum.SQL_EXCEPTION) {
						exception=e.getMessage();
					}
				}
				if(messageType!=LogMessageEnum.SKIP) {
					logService.addLog(migrateModel,messageType,migrateRowDataHandler.handleForLog(migrateModel.getTableModel(),row),exception);
				}
			}
		}
	}
	
	/**
	 * 批次处理结果集
	 * 不处理SQL异常信息，直接抛出
	 * @param migrateModel
	 * @param result
	 * @param executeSql
	 */
	private void batchProcess(MigrateModel migrateModel,List<Map<String,Object>> result,String executeSql)throws RuntimeException {
		List<Object[]> batchDataList=new ArrayList<>(result.size());
		
		List<LogModel> logModelList=processResult(migrateModel, result, batchDataList);
		
		//保存数据
		try {
			if(batchDataList.size()>0){
				sqlOperation.executeBatchDest(executeSql, batchDataList);
				//打印输出执行语句
				logService.logSql(executeSql,batchDataList);
			}
			//保存日志
			if(null!=logModelList&&logModelList.size()>0) {
				logService.insert(logModelList);
			}
		}finally {
			if(null!=batchDataList) {
				batchDataList.clear();
			}
			if(null!=batchDataList) {
				logModelList.clear();
			}
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
				List<Object[]> subDataList=null;
				LogMessageEnum messageType=LogMessageEnum.SUCCESS;
				
				try {
					if(null!=migrateModel.getTableModel().getMain()) {//处理子表的数据拆分
						subDataList=processSplitRowData(migrateModel,row);
					}else {
						data = processRowData(migrateModel, row);
					}
				} catch (FormatException | DataNotFoundException | DataExistsException | SplitException | DataAlreadyDealedException e) {
					messageType=migrateExceptionHandler.handle(e);
				}
				
				if(null!=subDataList&&subDataList.size()>0) {
					batchDataList.addAll(subDataList);
				}
				if(null!=data) {
					batchDataList.add(data);
				}
				
				if(messageType!=LogMessageEnum.SKIP) {//防止子批次执行时记录已经正确处理过的数据
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
	 * @throws FormatException 
	 * @throws DataNotFoundException 
	 * @throws DataExistsException 
	 * @throws DataAlreadyDealedException 
	 */
	private Object[] processRowData(MigrateModel migrateModel,Map<String,Object> row) throws FormatException, DataNotFoundException, DataExistsException, DataAlreadyDealedException {
		
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
			
			boolean isExists=sqlOperation.isDestExists(targetSelectSql, migrateRowDataHandler.handleTargetConstraintCondtion(row,table));
			
			if(null!=table.getMain()&&!"".equals(table.getMain())) {
				//判断主表记录是否已经存在
				if(!isExists) {
					throw new DataNotFoundException("主记录不存在！");
				}
				//判断子表记录是否已经出来过，通过外键判断
				String subTargetSelectgSql=sqlBuilder.getTargetSelectSql(table,true);
				if(null!=subTargetSelectgSql) {
					if(sqlOperation.isDestExists(subTargetSelectgSql, migrateRowDataHandler.handleTargetConstraintCondtion(row,table))) {
						throw new DataExistsException("待插入的记录已经存在");
					}
				}
			}else {
				if(isExists) {
					if(null!=migrateModel.getParentLogId()) {
						throw new DataAlreadyDealedException("数据已正确处理了！");
					}
					throw new DataExistsException("待插入的记录已经存在");
				}
			}
		}
		
		//判断数据是否已经处理过了
		if(null!=migrateModel.getParentLogId()&&logService.isLogSuccess((Long)row.get(table.getSourcePkName()),migrateModel.getParentLogId())) {
			return null;
		}
		
		return migrateRowDataHandler.handleMigrateField(row,table);
	}
	
	/**
	 * 处理字段拆分
	 * @param migrateModel
	 * @param row
	 * @return
	 * @throws SplitException 
	 * @throws DataExistsException 
	 * @throws DataNotFoundException 
	 * @throws FormatException 
	 * @throws DataAlreadyDealedException 
	 */
	private List<Object[]> processSplitRowData(MigrateModel migrateModel,Map<String,Object> row) throws SplitException, FormatException, DataNotFoundException, DataExistsException, DataAlreadyDealedException{
		
		List<Map<String,Object>> subRowList=new ArrayList<>();
		
		int size=migrateRowDataHandler.handleSplitRowData(migrateModel.getTableModel(),row);
		
		List<Object[]> result=new ArrayList<>(size);
		
		List<SplitModel> splitModelList=migrateModel.getTableModel().getSplitFieldList();
		for(int i=0;i<size;i++) {
			Map<String,Object> tmp=new HashMap<>();
			//处理拆分字段
			for(SplitModel splitModel:splitModelList) {
				if(null==splitModel
						||tmp.containsKey(splitModel.getFiledName().toUpperCase())
						||null==row.get(splitModel.getFiledName().toUpperCase())) {
					continue;
				}
				List<Object> splitField=(List<Object>) row.get(splitModel.getFiledName().toUpperCase());
				if(splitField.size()!=size) {
					throw new SplitException("拆分"+splitModel.getFiledName()+"字段长度不匹配，长度应为："+size);
				}
				tmp.put(splitModel.getFiledName().toUpperCase(),splitField.get(i));
			}
			//拷贝其他字段到集合中
			for(String key:row.keySet()) {
				if(!tmp.containsKey(key)) {
					tmp.put(key, row.get(key));
				}
			}
			subRowList.add(tmp);
		}
		
		if(subRowList.size()>0) {
			for(int i=0;i<size;i++) {
				result.add(processRowData(migrateModel, subRowList.get(i)));
			}
		}
		
		return result;
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
