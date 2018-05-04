package com.ipph.migratecore.deal.exception;
/**
 * 专利格式化错误
 */
public class PatentFormatException extends FormatException{
	private static final long serialVersionUID = 1L;

	public PatentFormatException(String message){
		super(message);
	}
}
