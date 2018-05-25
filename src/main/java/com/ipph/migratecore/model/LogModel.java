package com.ipph.migratecore.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.Setter;

import com.ipph.migratecore.enumeration.LogMessageEnum;
import com.ipph.migratecore.enumeration.LogStatusEnum;

@Entity
@Setter
@Getter
public class LogModel implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue
	private Long id;
	@Column
	@Enumerated(EnumType.STRING)
	private LogStatusEnum status;
	@Column
	private Long tableId;
	@Column
	private String tableName;
	@Column
	private Long dataId;
	@Column
	private String dealData;
	@Column
	private Date createDate;
	@Column
	private String message;
	@Column
	private Long batchLogId;
	@Column
	@Enumerated(EnumType.STRING)
	private LogMessageEnum messageType;
	@Column(length=2048*2)
	private String exception;
	@Transient
	private PatentInfo patentInfo;
}
