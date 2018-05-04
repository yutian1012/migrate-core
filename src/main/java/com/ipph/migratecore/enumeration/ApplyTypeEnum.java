package com.ipph.migratecore.enumeration;

/**
 * 应用类型，字段或条件应用到目标表还是数据源表
 */
public enum ApplyTypeEnum {
	
	SOURCE("source"),TARGET("target");
	
	private String type;
	private ApplyTypeEnum(String type){
		this.type=type;
	}
	
	public String getType(){
		return this.type;
	}
}
