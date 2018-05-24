package com.ipph.migratecore.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.ipph.migratecore.dao.PatentDao;
import com.ipph.migratecore.patent.PatentInterfaceHttpClient;

@Service
@Transactional
public class PatentService {
	@Resource
	private PatentDao patentDao;
	@Resource
	private PatentInterfaceHttpClient patentInterfaceHttpClient;
	
	public String getPatent(String appNumber) {
		
		try {
			JSONObject json=patentInterfaceHttpClient.getPatentByAppNumber(appNumber);
			if(null!=json) {
				return json.toJSONString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
