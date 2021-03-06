package com.ipph.migratecore.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class PatentInfo {
	@Id
	@GeneratedValue
	private Long id;
	@Column(unique=true)
	private String appNumber;
	private String appName;
	@Column()
	private Date appDate;
	private String status;
	private String applicant;
	private String originApplicant;
}
