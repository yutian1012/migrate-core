package com.ipph.migratecore.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PatentUploadModel implements Serializable{
	@Id
	@GeneratedValue
	private Long id;
	@Column(name="patentNo")
	private String patentNo;
	@Column(name="costType")
	private String costType;
	@Column(name="patentName")
	private String patentName;
	@Column(name="requestDate")
	private Date requestDate;
	@Column(name="pctDate")
	private Date pctDate;
	@Column(name="requestCountry")
	private String requestCountry;
	@Column(name="firstRequest")
	private String firstRequest;
	@Column(name="transferMoney")
	private Integer transferMoney;
	
}
