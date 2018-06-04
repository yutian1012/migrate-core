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
	@Column(name="authDate")
	private Date authDate;
	@Column(name="requestCountry")
	private String requestCountry;
	@Column(name="firstRequest")
	private String firstRequest;
	@Column(name="transferMoney")
	private Integer transferMoney;
	@Column(name="publicNo")
	private String publicNo;
	@Column(name="publicDate")
	private Date publicDate;
	@Column(name="authNo")
	private String authNo;
	@Column(name="status")
	private String status;//审核状态
	@Column(name="linkMan")
	private String linkMan;
	@Column(name="linkTel")
	private String linkTel;
	@Column(name="address")
	private String address;
	@Column(name="batch")
	private String batch;
	@Column(name="bank")
	private String bank;
	@Column(name="bankAccount")
	private String bankAccount;
	@Column(name="result")
	private String result;//处理结论
	@Column(name="resultTime")
	private Date resultTime;
	@Column(name="createTime")
	private Date createTime;
	@Column(name="creator")
	private String creator;
	@Column(name="resultor")
	private String resultor;
	@Column(name="sourceType")
	private String sourceType;//处理类型
	@Column(name="errorAppNo")
	private String errorAppNo;//错误申请号
}
