package com.ipph.migratecore.sql;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SqlOperation {
	@Resource
	private SqlBuilder sqlBuilder;
	
	@Resource(name="sourceJdbcTemplate")
	private JdbcTemplate sourceJdbcTemplate;
	
	@Resource(name="destJdbcTemplate")
	private JdbcTemplate destJdbcTemplate;
	
	/**
	 * 获取数据
	 * @param select
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getSourceData(String select,Object[] params){
		return sourceJdbcTemplate.queryForList(select,params);
	}
	/**
	 * 判断目标待更新的记录是否存在
	 * @param toUpdSelect
	 * @param params
	 * @return
	 */
	public boolean isDestExists(String destSelect,Object[] params){
		if(destSelect!=null){
			
			return getTotal(destJdbcTemplate, destSelect, params)>0L;
		}
		return false;
	}
	/**
	 * 获取查询的记录数量
	 * @param selectCount
	 * @param params
	 * @return
	 */
	public long getSourceTotal(String sourceSelect,Object[] params){
		
		return getTotal(sourceJdbcTemplate,sourceSelect,params);
	}
	/**
	 * 获取记录统计数量
	 * @param jdbcTemplate
	 * @param sql
	 * @param params
	 * @return
	 */
	private long getTotal(JdbcTemplate jdbcTemplate,String sql,Object[] params) {
		
		Map<String, Object> count=jdbcTemplate.queryForMap(sql,params);
		long total=0;
		
		if(null!=count.get("num")&&(Long)count.get("num")>0L){//唯一键是否重复
			total=(Long)count.get("num");
		}
	
		return total;
	}
	
	/**
	 * 执行操作
	 * @param update
	 * @param params
	 * @return
	 */
	public int executeDest(String sql,Object[] params){
		return destJdbcTemplate.update(sql, params);
	}
	
	/**
	 * 使用编程式事务控制批量数据处理
	 * @param sql
	 * @param batchDataList
	 * @return
	 */
	public int[] executeBatchDest(final String sql,final List<Object[]> batchDataList){
		
		return destJdbcTemplate.batchUpdate(sql,batchDataList);
	}
	
}
