package com.ipph.migratecore.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.ipph.migratecore.enumeration.LogMessageEnum;

import javax.persistence.GeneratedValue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class PatentInfoCheck implements Serializable{
	@Id
	@GeneratedValue
	private Long id;
	private Long oId;
	@Enumerated(EnumType.STRING)
	private LogMessageEnum type;
	private String status;
	private String appNumbers;//申请号集合，分号分隔
	private Long batchLogId;
	private String oAppNumber;//原申请号
	private String costType;//补助类型
}
