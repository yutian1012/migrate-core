package com.ipph.migratecore.deal.exception;

public class DataAlreadyDealed extends Exception{
	private static final long serialVersionUID = 1L;

	public DataAlreadyDealed(String message){
		super(message);
	}
}