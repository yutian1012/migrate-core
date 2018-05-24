package com.ipph.migratecore.deal.exception;

public class DataAlreadyDealedException extends Exception{
	private static final long serialVersionUID = 1L;

	public DataAlreadyDealedException(String message){
		super(message);
	}
}