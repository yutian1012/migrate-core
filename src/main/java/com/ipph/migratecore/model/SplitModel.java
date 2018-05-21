package com.ipph.migratecore.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 对象xml的format信息，记录格式化信息设置
 * 一般出现在迁移的目标Table设置中，放到源table无效
 */
@Setter
@Getter
@NoArgsConstructor
public class SplitModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private Class<?> clazz;
	private String splitParameter;
	private String filedName;
	private String defaultValue;
}
