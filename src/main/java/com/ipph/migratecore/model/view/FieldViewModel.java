package com.ipph.migratecore.model.view;

import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.enumeration.FieldValueTypeEnum;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FieldViewModel{
	private String fieldName;
	private String textValue;
	private String fieldValue;
	private boolean showText;//控制页面显示的布尔值
	private boolean showSelect;//控制页面显示的布尔值
	private String note;
	private ApplyTypeEnum applyType;
	private FieldValueTypeEnum valueType;
	private boolean forLog;
}
