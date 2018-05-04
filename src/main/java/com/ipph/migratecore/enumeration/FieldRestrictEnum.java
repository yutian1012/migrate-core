package com.ipph.migratecore.enumeration;

public enum FieldRestrictEnum {
	UNIQUE("唯一"),FOREIGN("外键"),PRIMARY("主键");
	private String name;
	private FieldRestrictEnum(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}
}
