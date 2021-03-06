package com.ipph.migratecore.enumeration;

public enum LogMessageEnum {
	SUCCESS("操作成功"),FORMART_EXCEPTION("数据格式化错误"),NOFOUND_EXCEPTION("数据未检索到"),DATAEXISTS_EXCEPTION("待插入的数据已经存在"),OTHERS("其他错误")
	,SQL_EXCEPTION("SQL语句执行错误"),SKIP("忽略该异常");
	private String value;
	
	private LogMessageEnum(String value) {
		this.value=value;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
