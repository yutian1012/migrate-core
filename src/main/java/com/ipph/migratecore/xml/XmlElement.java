package com.ipph.migratecore.xml;

public interface XmlElement {
	public static final String table="table";//表标签
	public static final String subTable="subTable";//子表标签
	public static final String field="field";//字段属性标签
	public static final String format="format";//指定format标签
	public static final String field_condition="condition";//字段条件字段
	public static final String where="where";//where条件设置
	public static final String constraint="constraint";//字段限定（主键，外键等）
}
