package com.ipph.migratecore.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.ipph.migratecore.enumeration.TableOperationEnum;

/**
 * 该类对象fromTable和toTable标签，表示一张表信息
 */
@Entity
public class TableModel implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue
	private Long id;
	@Column
	private TableOperationEnum type;
	@Column
	private String fieldListJson;
	@Transient
	private List<FieldModel> filedList;
	@Column
	private String subTableListJson;
	@Transient
	private List<SubtableModel> subTableList;
	@Column
	private String formatFieldListJson;
	@Transient
	private List<FormatModel> formatFieldList;
	@Column
	private String whereJson;
	@Transient
	private WhereModel whereModel;
	@Column(name="sourceTable")
	private String from;
	@Column(name="destTable")
	private String to;
	@Column
	private boolean skip;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSubTableListJson() {
		return subTableListJson;
	}
	public void setSubTableListJson(String subTableListJson) {
		this.subTableListJson = subTableListJson;
	}
	public String getFormatFieldListJson() {
		return formatFieldListJson;
	}
	public void setFormatFieldListJson(String formatFieldListJson) {
		this.formatFieldListJson = formatFieldListJson;
	}
	public String getWhereJson() {
		return whereJson;
	}
	public void setWhereJson(String whereJson) {
		this.whereJson = whereJson;
	}
	public TableOperationEnum getType() {
		return type;
	}
	public void setType(TableOperationEnum type) {
		this.type = type;
	}
	public List<FieldModel> getFiledList() {
		return filedList;
	}
	public void setFiledList(List<FieldModel> filedList) {
		this.filedList = filedList;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public boolean isSkip() {
		return skip;
	}
	public void setSkip(boolean skip) {
		this.skip = skip;
	}
	public List<SubtableModel> getSubTableList() {
		return subTableList;
	}
	public void setSubTableList(List<SubtableModel> subTableList) {
		this.subTableList = subTableList;
	}
	public WhereModel getWhereModel() {
		return whereModel;
	}
	public void setWhereModel(WhereModel whereModel) {
		this.whereModel = whereModel;
	}
	public List<FormatModel> getFormatFieldList() {
		return formatFieldList;
	}
	public void setFormatFieldList(List<FormatModel> formatFieldList) {
		this.formatFieldList = formatFieldList;
	}
	public String getFieldListJson() {
		return fieldListJson;
	}
	public void setFieldListJson(String fieldListJson) {
		this.fieldListJson = fieldListJson;
	}
}
