package com.ipph.migratecore.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PatentUploadModel implements Serializable{
	@Id
	@GeneratedValue
	private Long id;
	@Column(name="patentNo")
	private String patentNo;
	@Column(name="costType")
	private String costType;
}
