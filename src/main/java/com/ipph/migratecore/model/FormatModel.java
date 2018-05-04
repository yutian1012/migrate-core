package com.ipph.migratecore.model;

/**
 * 对象xml的format信息，记录格式化信息设置
 * 一般出现在迁移的目标Table设置中，放到源table无效
 */
public class FormatModel implements Cloneable{
	private Class<?> clazz;
	private String formatParameter;
	private String filedName;
	
	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getFormatParameter() {
		return formatParameter;
	}

	public void setFormatParameter(String formatParameter) {
		this.formatParameter = formatParameter;
	}

	public String getFiledName() {
		return filedName;
	}

	public void setFiledName(String filedName) {
		this.filedName = filedName;
	}

	public FormatModel copyFormatModel(){
		try {
			return (FormatModel) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
