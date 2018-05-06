package com.ipph.migratecore.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.ipph.migratecore.enumeration.BatchStatusEnum;

@Entity
public class BatchModel implements Serializable{
	@Id
    @GeneratedValue
    private Long id;
	@Column
	private int size;
	@Column
	private Date createDate;
	@Column
	private BatchStatusEnum status;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public BatchStatusEnum getStatus() {
		return status;
	}
	public void setStatus(BatchStatusEnum status) {
		this.status = status;
	}
}
