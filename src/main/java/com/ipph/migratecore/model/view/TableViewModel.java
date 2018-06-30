package com.ipph.migratecore.model.view;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.enumeration.FieldValueTypeEnum;
import com.ipph.migratecore.enumeration.TableOperationEnum;
import com.ipph.migratecore.model.TableModel;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TableViewModel {

	private Long id;
	private TableOperationEnum type;
	private String from;
	private String to;
	private boolean skip;
	private String note;
	
	private List<FieldViewModel> fieldList;
	
}

