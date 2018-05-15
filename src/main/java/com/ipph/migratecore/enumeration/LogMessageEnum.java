package com.ipph.migratecore.enumeration;

public enum LogMessageEnum {
	SUCCESS("操作成功"),FORMART_EXCEPTION("数据格式化错误"),NOFOUND_EXCEPTION("数据未检索到");
	private String value;
	
	private LogMessageEnum(String value) {
		this.value=value;
	}
	public String getValue() {
		return this.value;
	}
}
