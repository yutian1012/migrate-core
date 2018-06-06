package com.ipph.migratecore.table;

import java.util.ArrayList;
import java.util.List;

public class TableModel {
	//表名
	private String name="";
	//表注释
	private String comment="";
	//列列表
	private List<ColumnModel> columnList=new ArrayList<ColumnModel>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComment() {
		if(null!=comment&&!"".equals(comment)){
			comment=comment.replace("'", "''");
		}
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * 添加列对象。
	 * @param model
	 */
	public void addColumnModel(ColumnModel model){
		this.columnList.add(model);
	}
	
	public List<ColumnModel> getColumnList() {
		return columnList;
	}
	public void setColumnList(List<ColumnModel> columnList) {
		this.columnList = columnList;
	}
	
	
	public List<ColumnModel> getPrimayKey(){
		List<ColumnModel> pks=new ArrayList<ColumnModel>();
		for(ColumnModel column:columnList){
			if(column.getIsPk()){
				pks.add(column);
			}
		}
		return pks;
	}
	@Override
	public String toString() {
		return "TableModel [name=" + name + ", comment=" + comment + ", columnList=" + columnList + "]";
	}
}
