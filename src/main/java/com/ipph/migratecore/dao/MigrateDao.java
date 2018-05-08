package com.ipph.migratecore.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.ipph.migratecore.deal.MigrateExceptionHandler;
import com.ipph.migratecore.deal.MigrateRowDataHandler;
import com.ipph.migratecore.deal.exception.ConfigException;
import com.ipph.migratecore.deal.exception.DataNotFoundException;
import com.ipph.migratecore.deal.exception.FormatException;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.service.LogService;
import com.ipph.migratecore.sql.SqlBuilder;
import com.ipph.migratecore.sql.SqlOperation;
import com.ipph.migratecore.util.MapUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
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
	 */
	public void update(TableModel table) throws ConfigException {
		
		//判断是否忽略该表的迁移操作
		if(null==table|| table.isSkip()) return;
		
		//第一步：获取数据源表的select语句--使用该语句实现数据源的查询
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		//第二步：获取待更新表的update语句--使用该语句实现目标数据字段的更新
		String update=sqlBuilder.getUpdateSql(table);
		
		//update语句必须要有条件，否则直接返回--校验操作
		if(null==update||update.indexOf("?")==-1) {
			throw new ConfigException("更新配置错误，未对更新数据进行条件限定！！！");
		}
		
		//第三步：获取更新操作记录是否存在的select语句--使用该语句判断待更新的数据是否存在
		String targetSelect=sqlBuilder.getTargetSelectSql(table);
		
		//第四步：获取数据源的查询结果集--该数据集用于更新目标数据
		List<Map<String,Object>> result=sqlOperation.getSourceData(select,migrateRowDataHandler.handleSourceFieldCondition(table));
		
		if(result!=null){
			for(Map<String,Object> row:result){
				//记录数据
				logService.log(table);
				try{
					if(null!=targetSelect){
						//判断待更新的数据是否存在
						if(!sqlOperation.isDestExists(targetSelect, migrateRowDataHandler.handleTargetFieldCondtion(row,table))){
							throw new DataNotFoundException("未找到更新记录");
						}
					}
					//第五步：执行更新数据
					int upd=sqlOperation.executeDest(update, migrateRowDataHandler.handleMigrateField(row,table));
					//第六步：记录日志--用于跟踪数据操作审计
					if(upd>0){
						if(log.isDebugEnabled()){
							log.debug("update the data success!");
						}
					}else{
						log.error("更新失败"+MapUtil.outMapData(row));
					}
				}catch(FormatException e){
					migrateExceptionHandler.formatExceptionHandler(e, row, table,true);
				}catch(DataNotFoundException e){
					migrateExceptionHandler.dataNotFoundExceptionHandler(e,row, table);
				}catch(Exception e){
					migrateExceptionHandler.sqlExceptionHandler(e, row, table,true);
				}
				
			}
		}
	}
}