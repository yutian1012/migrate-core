package com.ipph.migratecore.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.ipph.migratecore.deal.MigrateExceptionHandler;
import com.ipph.migratecore.deal.MigrateRowDataHandler;
import com.ipph.migratecore.deal.exception.ConfigException;
import com.ipph.migratecore.deal.exception.DataNotFoundException;
import com.ipph.migratecore.deal.exception.FormatException;
import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.service.LogService;
import com.ipph.migratecore.sql.SqlBuilder;
import com.ipph.migratecore.sql.SqlOperation;
import com.ipph.migratecore.util.MapUtil;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MigrateDao {
	
	@Resource
	private SqlOperation sqlOperation;
	@Resource
	private SqlBuilder sqlBuilder;
	
	@Resource
	private MigrateRowDataHandler migrateRowDataHandler;
	
	@Resource
	private MigrateExceptionHandler migrateExceptionHandler;
	
	@Resource
	private  LogService logService;
	/**
	 * 数据表更新操作
	 * @param table
	 * @throws ConfigException 
	 * @throws SQLException 
	 */
	public void update(TableModel table,Long batchLogId,Long parentLogId) throws ConfigException, SQLException {
		
		//判断是否忽略该表的迁移操作
		if(null==table|| table.isSkip()) return;
		
		//判断条件限定中是否存在主键
		if(!sqlBuilder.hasPrimaryKey(table)) {
			throw new ConfigException("配置错误，源表中必须存在主键字段！！！");
		}
		
		//第一步：获取数据源表的select语句--使用该语句实现数据源的查询
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		//第二步：获取待更新表的update语句--使用该语句实现目标数据字段的更新
		String update=sqlBuilder.getUpdateSql(table);
		
		//update语句必须要有条件，否则直接返回--校验操作
		if(null==update||update.indexOf("?")==-1) {
			throw new ConfigException("配置错误，未对更新数据进行条件限定！！！");
		}
		
		//第三步：获取更新操作记录是否存在的select语句--使用该语句判断待更新的数据是否存在
		String targetSelect=sqlBuilder.getTargetSelectSql(table);
		
		if(log.isDebugEnabled()) {
			log.debug("get source data from "+table.getFrom());
		}
		
		//第四步：获取数据源的查询结果集--该数据集用于更新目标数据
		List<Map<String,Object>> result=sqlOperation.getSourceData(select,migrateRowDataHandler.handleSourceFieldCondition(table));
		
		//第五步：执行更新数据--遍历集合执行相关操作
		if(result!=null){
			for(Map<String,Object> row:result){
				//在存在父批次时，判断改记录是否已经成功执行过了
				if(null==parentLogId||!logService.isLogSuccess((Long)row.get(table.getSourcePkName()),parentLogId)) {
					dealData(table, row, targetSelect, update,batchLogId);
				}
			}
		}
	}
	
	/**
	 * 处理数据并捕获相应异常从而记录日志
	 * @param table
	 * @param row
	 * @param targetSelect
	 * @param executeSql
	 */
	private void dealData(TableModel table,Map<String,Object> row,String targetSelect,String executeSql,Long batchLogId) {
		
		if(log.isDebugEnabled()) {
			log.debug("migrate data "+MapUtil.outMapData(row));
		}
		
		LogStatusEnum status=LogStatusEnum.SUCCESS;
		String message="操作成功";
		
		//记录每一行数据的操作日志
		Long logId=logService.log(table,migrateRowDataHandler.handleForLog(table,row),batchLogId);
		
		try {
			deal(table, row, targetSelect, executeSql);
		} catch (DataNotFoundException | FormatException e) {
			//e.printStackTrace();
			status=LogStatusEnum.FAIL;
			message=e.getMessage();
		}
		
		logService.updateLog(logId, status, message);
	}
	
	/**
	 * 处理数据
	 * @param table
	 * @param row
	 * @param targetSelect
	 * @param executeSql
	 * @throws DataNotFoundException 
	 * @throws FormatException 
	 */
	private void deal(TableModel table,Map<String,Object> row,String targetSelect,String executeSql) throws DataNotFoundException, FormatException {
		
		if(null!=targetSelect){
			//判断待更新的数据是否存在
			if(!sqlOperation.isDestExists(targetSelect, migrateRowDataHandler.handleTargetFieldCondtion(row,table))){
				throw new DataNotFoundException("未找到更新记录");
			}
		}
		sqlOperation.executeDest(executeSql, migrateRowDataHandler.handleMigrateField(row,table));
		
		//int upd=sqlOperation.executeDest(executeSql, migrateRowDataHandler.handleMigrateField(row,table));
		/*if(upd>0){
			if(log.isDebugEnabled()){
				log.debug("update the data success!");
			}
		}else{
			log.error("更新失败"+MapUtil.outMapData(row));
		}*/
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