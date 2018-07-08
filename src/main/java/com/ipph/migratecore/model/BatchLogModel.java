package com.ipph.migratecore.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.ipph.migratecore.enumeration.BatchStatusEnum;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class BatchLogModel implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue
	private Long id;
	@Column
	private Long batchId;//关联批次信息
	@Column
	private String batchName;
	@Column
	private Long parentId;
	@Column
	private String batchNo;//批次号
	@Column
	private int size;
	@Column
	private int success;
	@Column
	private Date createDate;
	@Column
	@Enumerated(EnumType.STRING)
	private BatchStatusEnum status;
}
