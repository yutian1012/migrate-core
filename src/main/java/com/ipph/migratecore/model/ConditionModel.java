package com.ipph.migratecore.model;

import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.enumeration.FieldConditionTypeEnum;

public class ConditionModel implements Cloneable {
	private FieldConditionTypeEnum type;
	private FieldModel field;
	private ApplyTypeEnum applyType;
	public FieldConditionTypeEnum getType() {
		return type;
	}
	public void setType(FieldConditionTypeEnum type) {
		this.type = type;
	}
	public FieldModel getField() {
		return field;
	}
	public void setField(FieldModel field) {
		this.field = field;
	}
	public ApplyTypeEnum getApplyType() {
		return applyType;
	}
	public void setApplyType(ApplyTypeEnum applyType) {
		this.applyType = applyType;
	}
	public ConditionModel copyConditionModel(){
		try {
			return (ConditionModel) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
