package com.ipph.migratecore.model;

import java.io.Serializable;

import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.enumeration.FieldDataTypeEnum;
import com.ipph.migratecore.enumeration.FieldValueTypeEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * 该类对象xml的field标签
 */
@Setter
@Getter
public class FieldModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private String value;
	private String note;
	private FieldDataTypeEnum fieldType;
	private ApplyTypeEnum applyType;
	private FieldValueTypeEnum valueType;
	private boolean forLog;
}
