package com.ipph.migratecore.deal.split;

import java.util.List;

import com.ipph.migratecore.deal.exception.SplitException;

public interface Spliter {
	/**
	 * 字段拆分
	 * @param args
	 * @param value
	 * @return
	 */
	public List<Object> split(String args,Object value) throws SplitException;
}
