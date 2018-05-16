package com.ipph.migratecore.model;

import java.io.Serializable;

import com.ipph.migratecore.enumeration.TableOperationEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MigrateModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private TableOperationEnum type;
	private TableModel tableModel;
	private Long batchLogId;
	private Long parentLogId;
	private Long total;
	private int start;
	private int size;
}
