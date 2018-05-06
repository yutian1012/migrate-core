package com.ipph.migratecore.model;

import java.io.Serializable;

import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.enumeration.FieldDataTypeEnum;
import com.ipph.migratecore.enumeration.FieldValueTypeEnum;

/**
 * 该类对象xml的field标签
 */
public class FieldModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private String value;
	private FieldDataTypeEnum fieldType;
	private ApplyTypeEnum applyType;
	private FieldValueTypeEnum valueType;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public FieldDataTypeEnum getFieldType() {
		return fieldType;
	}
	public void setFieldType(FieldDataTypeEnum fieldType) {
		this.fieldType = fieldType;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public ApplyTypeEnum getApplyType() {
		return applyType;
	}
	public void setApplyType(ApplyTypeEnum applyType) {
		this.applyType = applyType;
	}
	public FieldValueTypeEnum getValueType() {
		return valueType;
	}
	public void setValueType(FieldValueTypeEnum valueType) {
		this.valueType = valueType;
	}
}
