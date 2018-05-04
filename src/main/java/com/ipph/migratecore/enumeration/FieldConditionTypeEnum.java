package com.ipph.migratecore.enumeration;

public enum FieldConditionTypeEnum {
	LIKE("like"),LLike("like"),RLIKE("like"),EQUAL("="),NEQUAL("!="),IN("in"),NOTIN("not in"),GT(">"),GE(">="),LT("<"),LE("<="),ISNULL("is null"),ISNOTNULL("is not null");
	private String name;
	private FieldConditionTypeEnum(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}
}
