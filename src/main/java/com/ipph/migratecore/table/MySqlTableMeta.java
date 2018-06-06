package com.ipph.migratecore.table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ipph.migratecore.util.StringUtil;

public abstract class MySqlTableMeta extends BaseTableMeta{

	private final String  SQL_GET_COLUMNS="SELECT" +
			" TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,CHARACTER_OCTET_LENGTH LENGTH," +
			" NUMERIC_PRECISION PRECISIONS,NUMERIC_SCALE SCALE,COLUMN_KEY,COLUMN_COMMENT,COLUMN_TYPE " +
			" FROM " +
			" INFORMATION_SCHEMA.COLUMNS " +
			" WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='%s' ";
	
	private final String  SQL_GET_COLUMNS_BATCH="SELECT" +
			" TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,CHARACTER_OCTET_LENGTH LENGTH," +
			" NUMERIC_PRECISION PRECISIONS,NUMERIC_SCALE SCALE,COLUMN_KEY,COLUMN_COMMENT,COLUMN_TYPE " +
			" FROM " +
			" INFORMATION_SCHEMA.COLUMNS " +
			" WHERE TABLE_SCHEMA=DATABASE() ";
	
	private final String sqlComment ="select table_name,table_comment  from information_schema.tables t where t.table_schema=DATABASE() and table_name='%s' ";
	
	//获取数据库中的表信息列表
	private final String sqlAllTable="select table_name,table_comment from information_schema.tables t where t.table_type='BASE TABLE' AND t.table_schema=DATABASE()";

	private final String sqlPk="SELECT k.column_name name "
			+"FROM information_schema.table_constraints t "
			+"JOIN information_schema.key_column_usage k "
			+"USING(constraint_name,table_schema,table_name) "
			+"WHERE t.constraint_type='PRIMARY KEY' "
  			+"AND t.table_schema=DATABASE() "
  			+"AND t.table_name='%s'";
	

	/**
	 * 获取表对象
	 */
	@Override
	public TableModel getTableByName(String tableName) {
		TableModel model=getTableModel(tableName);
		//获取列对象
		List<ColumnModel> columnList= getColumnsByTableName(tableName);
		model.setColumnList(columnList);
		return model;
	}
	
	/**
	 * 根据表名获取主键列名
	 * @param tableName
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getPkColumn(String tableName){
		String sql=String.format(sqlPk, tableName);
		Object rtn=getJdbcTemplate().queryForObject(sql, null, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int row)
					throws SQLException {
				 return rs.getString("name");
			}
		});
		if(rtn==null)
			return "";
		 
		return rtn.toString();
	}
	
	/**
	 * 根据表名获取列
	 * @param tableName
	 * @return
	 */
	private List<ColumnModel> getColumnsByTableName(String tableName){
		String sql=String.format(SQL_GET_COLUMNS, tableName);
		Map<String,Object> map=new HashMap<String,Object>();
		//sqlColumns语句的column_key包含了column是否为primary key，并在MySqlColumnMap中进行了映射。
		
		List<ColumnModel> list= getJdbcTemplate().query(sql, new Object[] {}, new MySqlColumnMap());//JdbcTemplateUtil.getNamedParameterJdbcTemplate(jdbcTemplate).query(sql, map, new MySqlColumnMap());
		for(ColumnModel model:list){
			model.setTableName(tableName);
		}
		return list;
	}
	/**
	 * 根据表名获取列。此方法使用批量查询方式。
	 * @param tableName
	 * @return
	 */
	private Map<String,List<ColumnModel>> getColumnsByTableName(List<String> tableNames){
		String sql=SQL_GET_COLUMNS_BATCH;
		Map<String, List<ColumnModel>> map = new HashMap<String, List<ColumnModel>>();
		if(tableNames!=null && tableNames.size()==0){
			return map;
		}else{
			StringBuffer buf=new StringBuffer();
			for(String str:tableNames){
				buf.append("'"+str+"',");
			}
			buf.deleteCharAt(buf.length()-1);
			sql+=" AND TABLE_NAME IN ("+buf.toString()+") ";
		}
//		jdbcHelper.setCurrentDb(currentDb);
//		jdbcHelper=JdbcHelper.getInstance();
		List<ColumnModel> columnModels= getJdbcTemplate().query(sql, new Object[] {}, new MySqlColumnMap());//JdbcTemplateUtil.getNamedParameterJdbcTemplate(jdbcTemplate).query(sql, new HashMap<String,Object>(), new MySqlColumnMap());
		for(ColumnModel columnModel:columnModels){
			String tableName= columnModel.getTableName();
			if(map.containsKey(tableName)){
				map.get(tableName).add(columnModel);
			}else{
				List<ColumnModel> cols=new ArrayList<ColumnModel>();
				cols.add(columnModel);
				map.put(tableName, cols);
			}
		}
		return map;
	}
	
	/**
	 * 根据表名获取tableModel。
	 * @param tableName
	 * @return
	 */
	private TableModel getTableModel(final String tableName){
	
		String sql=String.format(sqlComment, tableName);
		TableModel tableModel= (TableModel) getJdbcTemplate().queryForObject(sql, null, new RowMapper<TableModel>() {

			@Override
			public TableModel mapRow(ResultSet rs, int row)
					throws SQLException {
				TableModel tableModel=new TableModel();
				String comments=rs.getString("table_comment");
				comments=getComments(comments,tableName);
				tableModel.setName(tableName);
				tableModel.setComment(comments);
				return tableModel;
			}
		});
		if(null!=tableModel)
			tableModel=new TableModel();
		
		return tableModel;
	}
	/**
	 * 获取注释
	 * @param comments
	 * @param defaultValue
	 * @return
	 */
	public static String getComments(String comments,String defaultValue){
		if(null==comments||"".equals(comments)) return defaultValue;
		int idx=comments.indexOf("InnoDB free");
		if(idx>-1){
			comments=comments.substring(0,idx).trim();//StringUtils.trimSufffix(comments.substring(0,idx).trim(),";");
		}
		if(StringUtil.isEmpty(comments)){
			comments=defaultValue;
		}
		return comments;
	}

	@Override
	public Map<String, String> getTablesByTableNameList(List<String> names) {
		StringBuffer sb=new StringBuffer();
		for(String name:names){
			sb.append("'");
			sb.append(name);
			sb.append("',");
		}
		sb.deleteCharAt(sb.length()-1);
		String sql=sqlAllTable+ " and  lower(table_name) in (" + sb.toString().toLowerCase() +")";
		
		Map<String,String> parameter=new HashMap<String,String>();
		List<Map<String,String>> list=getJdbcTemplate().query(sql, new Object[] {}, new RowMapper<Map<String,String>>() {//JdbcTemplateUtil.getNamedParameterJdbcTemplate(jdbcTemplate).query(sql, parameter, new RowMapper<Map<String,String>>() {
			@Override
			public Map<String,String> mapRow(ResultSet rs, int row)
					throws SQLException {
				String tableName=rs.getString("table_name");
				String comments=rs.getString("table_comment");
				Map<String,String> map=new HashMap<String, String>();
				map.put("tableName", tableName);
				map.put("tableComment", comments);
				return map;
			}
		});
		Map<String, String> map=new LinkedHashMap<String, String>();
		for(int i=0;i<list.size();i++){
			Map<String,String> tmp=list.get(i);
			String name=tmp.get("tableName");
			String comments=tmp.get("tableComment");
			map.put(name, comments);
		}
	
		return map;
	}
	/**
	 * 通过表名查询数据
	 */
	@Override
	public List<TableModel> getTablesByName(String tableName) throws Exception {
		String sql=sqlAllTable;
		if(StringUtil.isNotEmpty(tableName))
			sql +=" AND TABLE_NAME LIKE '%" +tableName +"%'";
		
		RowMapper<TableModel> rowMapper=new RowMapper<TableModel>() {
			@Override
			public TableModel mapRow(ResultSet rs, int row)
					throws SQLException {
				TableModel tableModel=new TableModel();
				tableModel.setName(rs.getString("TABLE_NAME"));
				String comments=rs.getString("TABLE_COMMENT");
				comments=getComments(comments,tableModel.getName());
				tableModel.setComment(comments);
				return tableModel;
			}
		};
		List<TableModel> tableModels=getJdbcTemplate().query(sql,  rowMapper);
		
		List<String> tableNames=new ArrayList<String>();
		//get all table names
		for(TableModel model:tableModels){
			tableNames.add(model.getName());
		}
		Map<String,List<ColumnModel>> tableColumnsMap = getColumnsByTableName(tableNames);
		for(Entry<String, List<ColumnModel>> entry:tableColumnsMap.entrySet()){
			for(TableModel model:tableModels){
				if(model.getName().equalsIgnoreCase(entry.getKey())){
					model.setColumnList(entry.getValue());
				}
			}
		}
		return tableModels;

	}
	/**
	 * 获取jdbcTemplate
	 * @return
	 */
	protected abstract JdbcTemplate getJdbcTemplate();
}