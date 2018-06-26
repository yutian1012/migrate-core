package com.ipph.migratecore.controller;

import java.util.Map;

import com.ipph.migratecore.common.ExceptionMsg;
import com.ipph.migratecore.common.Response;

public class BaseController {
	protected Response result(ExceptionMsg msg){
    	return new Response(msg);
    }
	
	protected Response result(ExceptionMsg msg,Map<String,Object> models){
    	return new Response(msg,models);
    }
}
