package com.ipph.migratecore.model;

import java.io.Serializable;

import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.enumeration.FieldConditionTypeEnum;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConditionModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private FieldConditionTypeEnum type;
	private FieldModel field;
	private ApplyTypeEnum applyType;
}
