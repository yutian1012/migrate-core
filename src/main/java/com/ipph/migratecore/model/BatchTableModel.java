package com.ipph.migratecore.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class BatchTableModel {
	@Id
	@GeneratedValue
	private Long id;
	@Column(nullable=false)
	private Long batchId;
	@Column(nullable=false)
	private Long TableId;
}
