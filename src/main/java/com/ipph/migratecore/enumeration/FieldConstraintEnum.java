package com.ipph.migratecore.enumeration;

public enum FieldConstraintEnum {
	PRIMARY("primary key"),FOREIGNKEY("foreign key");
	private String name;
	private FieldConstraintEnum(String name) {
		this.name=name;
	}
	public String getName() {
		return this.name;
	}
}
