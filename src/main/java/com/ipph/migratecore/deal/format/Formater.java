package com.ipph.migratecore.deal.format;

import com.ipph.migratecore.deal.exception.FormatException;

public interface Formater {
	/**
	 * 格式化方法，输出格式化后的数据值
	 * @param args
	 * @param value
	 * @return
	 */
	public Object format(String args,Object value) throws FormatException;
}
