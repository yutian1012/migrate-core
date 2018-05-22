package com.ipph.migratecore.model;

import java.io.Serializable;

import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.enumeration.FieldConstraintEnum;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConstraintModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private FieldConstraintEnum type;
	private FieldModel field;
	private ApplyTypeEnum applyType;
	private String value;
}
