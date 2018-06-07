package com.ipph.migratecore.enumeration;

import java.util.HashMap;
import java.util.Map;

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
	
	public static Map<String,String> getEnumValues(){
		Map<String, String> values=new HashMap<>();
		for(FieldValueTypeEnum valueType:FieldValueTypeEnum.values()) {
			values.put(valueType.name(), valueType.getType());
		}
		return values;
	}
}
