package com.ipph.migratecore.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CopyrightUploadModel implements Serializable{
	@Id
	@GeneratedValue
	private Long id;
	private String authorName;//作者
	private String ownerName;//著作权人名称
	private String workName;//作品名称
	private String workNo;//登记号
	private Date finishDate;//完成时间
	private Date createDate;//登记日期
	private String type;//类型
	private String paperNo;//证件号码
	private Date firstPublicDate;//发布日期
	private String firstPublicCity;//首次发布地点
	private String getRightWay;//权利获取方式
	private String RightOwnership;//权利归属方式
	private String RightOwnershipStatus;//权利拥有状况
	private String applyType;//申请方式
	private String applicantName;//提交者
	private String applyContacts;//申请联系人
	private Integer registFee;//登记费用
	private String area;//所属区域
	private String park;//所属园区
	private String note;//类型字段说明
}
