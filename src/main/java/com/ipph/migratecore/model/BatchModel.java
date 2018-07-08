package com.ipph.migratecore.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class BatchModel implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue
    private Long id;
	@Column
	private String batchName;
	@Column(length=128)
	private String note;
	@Column
	private Date createDate;
	@Transient//多对多的情况
	private List<TableModel> tableList;
}
