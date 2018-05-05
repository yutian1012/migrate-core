package com.ipph.migratecore.sql;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class SqlOperation {
	@Resource
	private SqlBuilder sqlBuilder;
	/*@Resource
	private JdbcTemplate fromJdbcTemplate;
	@Resource
	private JdbcTemplate toJdbcTemplate;*/
	
	/*@Resource
	private RowDataHandler rowDataHandler;*/
	
	/**
	 * 获取数据
	 * @param select
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getMigrateData(String select,Object[] params){
		//return fromJdbcTemplate.queryForList(select,params);
		return null;
	}
	/**
	 * 判断记录是否存在
	 * @param toUpdSelect
	 * @param params
	 * @return
	 */
	public boolean isExists(String toUpdSelect,Object[] params){
		/*if(toUpdSelect!=null){
			Map<String, Object> count=toJdbcTemplate.queryForMap(toUpdSelect,params);
			long total=0;
			
			if(null!=count.get("num")&&(Long)count.get("num")>0L){//唯一键是否重复
				total=(Long)count.get("num");
			}
		
			return total>0L;
		}*/
		return false;
	}
	
	public long getTotal(String selectCount,Object[] params){
		
		/*Map<String, Object> count=fromJdbcTemplate.queryForMap(selectCount,params);
		long total=0;
		
		if(null!=count.get("num")&&(Long)count.get("num")>0L){//唯一键是否重复
			total=(Long)count.get("num");
		}
	
		return total;*/
		
		return 0L;
	}
	/**
	 * 执行操作
	 * @param update
	 * @param params
	 * @return
	 */
	public int execute(String update,Object[] params){
		//return toJdbcTemplate.update(update, params);
		return 1;
	}
	
	public Map<String,Object> queryForMap(String limitSql,Object[] params){
		//return fromJdbcTemplate.queryForMap(limitSql,params);
		return null;
	}
	
	/**
	 * 使用编程式事务控制批量数据处理
	 * @param insert
	 * @param batchDataList
	 * @return
	 */
	public int[] migrate(final String insert,final List<Object[]> batchDataList){
		/*return transactionTemplate.execute(new TransactionCallback<int[]>() {
			@Override
			public int[] doInTransaction(TransactionStatus arg0) {
				int[] ins=toJdbcTemplate.batchUpdate(insert,batchDataList);
				return ins;
			}
		});*/
		return null;
	}
	
}
