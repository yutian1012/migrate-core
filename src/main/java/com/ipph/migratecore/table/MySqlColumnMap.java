package com.ipph.migratecore.table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.RowMapper;

public class MySqlColumnMap implements RowMapper<ColumnModel> {

	@Override
	public ColumnModel mapRow(ResultSet rs, int row) throws SQLException {
		ColumnModel column=new ColumnModel();

		String name=rs.getString("column_name");
		String is_nullable=rs.getString("is_nullable");
		String data_type=rs.getString("data_type");
		String length=rs.getString("length");
		String precisions=rs.getString("precisions");
		String scale=rs.getString("scale");
		String column_key=rs.getString("column_key");
		String column_comment=rs.getString("column_comment");
		String table_name=rs.getString("table_name");
		String column_type=rs.getString("column_type");
		column_comment=MySqlTableMeta.getComments(column_comment, name);
		int iLength;
		
		try{
			iLength=null==length||"".equals(length)?0:Integer.parseInt(length);
		}catch (NumberFormatException e) {
			iLength=0;
		}
		
		//ipph update 2016-3-14
		if(null!=column_type&&!"".equals(column_type)){
			String len=getColumnLength(column_type);
			if(null!=len) iLength=Integer.parseInt(len);
		}
		
		int iPrecisions=null==precisions||"".equals(precisions)?0:Integer.parseInt(precisions);
		
		int iScale=null==scale||"".equals(scale)?0:Integer.parseInt(scale);
		
		column.setName(name);
		column.setTableName(table_name);
		column.setComment(column_comment);
		if(null!=column_key && "PRI".equals(column_key))
			column.setIsPk(true);
		boolean isNull=is_nullable.equals("YES");
		column.setIsNull(isNull);
		
		setType(data_type,iLength,iPrecisions,iScale,column);
		
		return column;
	}
	

	/**
	 * 设置列类型
	 * @param dbtype
	 * @param length
	 * @param precision
	 * @param scale
	 * @param columnModel
	 * ipph update 2016-3-14
	 */
	private void setType(String dbtype,int length,int precision,int scale,ColumnModel columnModel)
	{
		if(dbtype.equals("bigint")) 
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_NUMBER);
			//columnModel.setIntLen(19);
			//columnModel.setDecimalLen(0);
			columnModel.setIntLen(length);
			columnModel.setDecimalLen(scale);
			return;
		}
			
		else if(dbtype.equals("int")) 
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_NUMBER);
			/*columnModel.setIntLen(10);
			columnModel.setDecimalLen(0);*/
			columnModel.setIntLen(length);
			columnModel.setDecimalLen(scale);
			return;
		}
		
		else if(dbtype.equals("mediumint")) 
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_NUMBER);
			/*columnModel.setIntLen(7);
			columnModel.setDecimalLen(0);*/
			columnModel.setIntLen(length);
			columnModel.setDecimalLen(scale);
			return;
		}

		
		else if( dbtype.equals("smallint") )
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_NUMBER);
			/*columnModel.setIntLen(5);
			columnModel.setDecimalLen(0);*/
			columnModel.setIntLen(length);
			columnModel.setDecimalLen(scale);
			return;
		}
		
		else if(dbtype.equals("tinyint")  )
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_NUMBER);
			/*columnModel.setIntLen(3);
			columnModel.setDecimalLen(0);*/
			columnModel.setIntLen(length);
			columnModel.setDecimalLen(scale);
			return;
		}
		
		else if(dbtype.equals("decimal")  )
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_NUMBER);
			columnModel.setIntLen(precision-scale);
			columnModel.setDecimalLen(scale);
			return;
		}
		
		else if(dbtype.equals("double")  )
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_NUMBER);
			/*columnModel.setIntLen(18);
			columnModel.setDecimalLen(4);*/
			columnModel.setIntLen(precision);
			columnModel.setDecimalLen(scale);
			return;
		}
		
		else if(dbtype.equals("float")  )
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_NUMBER);
			/*columnModel.setIntLen(8);
			columnModel.setDecimalLen(4);*/
			columnModel.setIntLen(precision);
			columnModel.setDecimalLen(scale);
			return;
		}
	
		
		else if(dbtype.equals("varchar")  )
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_VARCHAR);
			columnModel.setCharLen(length);
			return;
		}
		
		else if(dbtype.equals("char")  )
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_VARCHAR);
			columnModel.setCharLen(length);
			return;
		}
		
		else if(dbtype.startsWith("date"))
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_DATE);
			return;
		}
		
		else if(dbtype.startsWith("time"))
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_DATE);
			return;
		}
		
		else if(dbtype.equals("timestamp"))
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_DATE);
			return;
		}
		
		
		else if(dbtype.endsWith("text")  )
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_TEXT);
			//columnModel.setCharLen(65535);
			columnModel.setCharLen(length);
			return;
		}
		else if(dbtype.endsWith("blob")  )
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_CLOB);
			//columnModel.setCharLen(65535);
			columnModel.setCharLen(length);
			return;
		}
		else if(dbtype.endsWith("clob")  )
		{
			columnModel.setColumnType(ColumnModel.COLUMNTYPE_CLOB);
			//columnModel.setCharLen(65535);
			columnModel.setCharLen(length);
			return;
		}
	}
	/**
	 * ipph add 2016-3-14
	 * @param column_type
	 * @return
	 */
	private String getColumnLength(String column_type){
		String len=null;
		Pattern p=Pattern.compile("\\w+\\((\\d+)\\)");
		Matcher m=p.matcher(column_type.trim());
		if(m.matches()&&m.groupCount()>0){
			len=m.group(1);
		}
		return len;
	}

}
