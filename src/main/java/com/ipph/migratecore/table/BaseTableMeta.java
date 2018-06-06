package com.ipph.migratecore.table;

import java.util.List;
import java.util.Map;

public abstract class BaseTableMeta {

	/**
	 * 根据表名，取得表模型。此处对表名进行非模糊匹配。
	 * @param tableName 表名
	 * @return 表模型
	 */
	public abstract TableModel getTableByName(String tableName);

	/**
	 * 根据表名,使用模糊匹配，查询系统中的表。返回的用Map表示的数据对象。key=表名，value=表注解。
	 * 
	 * @param tableName
	 * @return
	 * @throws Exception 
	 */
	public abstract List<TableModel> getTablesByName(String tableName) throws Exception;
	
	/**
	 * 根据表名查询系统中的表。
	 * 返回的Map中：key=表名，value=表comment
	 * 
	 * @param tableName
	 * @return
	 */
	public abstract Map<String, String> getTablesByTableNameList(List<String> names);
	
}