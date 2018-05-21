package com.ipph.migratecore.enumeration;
/**
 * 数据字段值来源
 */
public enum FieldValueTypeEnum {
	FIXED("fiexd"),FIELD("field"),GENCODE("gencode");
	private String type;
	private FieldValueTypeEnum(String type){
		this.type=type;
	}
	public String getType(){
		return this.type;
	}
}
