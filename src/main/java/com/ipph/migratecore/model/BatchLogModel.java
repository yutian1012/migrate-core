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
	private Long size;
	@Column
	private Long total;
	@Column
	private Long num;//更新次数，如果次数大于10，而size不发生变动，则证明批次已经终止了
	@Column
	private Date createDate;
	@Column
	@Enumerated(EnumType.STRING)
	private BatchStatusEnum status;
}
