package com.ipph.migratecore.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.ipph.migratecore.enumeration.LogStatusEnum;

@Entity
public class LogModel implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue
	private Long id;
	@Column
	private Long batchId;
	@Column
	private Long parentBatchId;
	@Column
	private Long dataId;
	@Column
	private LogStatusEnum status;
	@Column
	private Long tableId;
	@Column
	private String tableName;
	@Column
	private String dealData;
	@Column
	private Date createDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getBatchId() {
		return batchId;
	}
	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}
	public Long getParentBatchId() {
		return parentBatchId;
	}
	public void setParentBatchId(Long parentBatchId) {
		this.parentBatchId = parentBatchId;
	}
	public Long getDataId() {
		return dataId;
	}
	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}
	public LogStatusEnum getStatus() {
		return status;
	}
	public void setStatus(LogStatusEnum status) {
		this.status = status;
	}
	public Long getTableId() {
		return tableId;
	}
	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getDealData() {
		return dealData;
	}
	public void setDealData(String dealData) {
		this.dealData = dealData;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
