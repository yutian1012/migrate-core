package com.ipph.migratecore.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ipph.migratecore.enumeration.TableOperationEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * 该类对象fromTable和toTable标签，表示一张表信息
 */
@Entity
@Setter
@Getter
@JsonIgnoreProperties(value={"handler","hibernateLazyInitializer"})
public class TableModel implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue
	private Long id;
	@Column
	private TableOperationEnum type;
	@Column(length=2048*2)
	private String fieldListJson;
	@Transient
	private List<FieldModel> filedList;
	@Column(length=2048*2)
	private String subTableListJson;
	@Transient
	private List<SubtableModel> subTableList;
	@Column(length=2048*2)
	private String formatFieldListJson;
	@Transient
	private List<FormatModel> formatFieldList;
	@Column(length=2048*2)
	private String whereJson;
	@Transient
	private WhereModel whereModel;
	@Column(name="sourceTable")
	private String from;
	@Column(name="destTable")
	private String to;
	@Column
	private boolean skip;
	@Column(length=2048*2)
	private String constraintListJson;
	@Transient
	private List<ConstraintModel> constraintList;
	@Column(length=2048)
	private String splitFieldListJson;
	@Transient
	private List<SplitModel> splitFieldList;
	@Transient
	private String sourcePkName;
	@Column
	private String note;
	@Column
	private String main;
}
