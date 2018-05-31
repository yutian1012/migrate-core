package com.ipph.migratecore.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.ipph.migratecore.dao.PatentUploadDao;
import com.ipph.migratecore.model.PatentUploadModel;

@Service
public class PatentUploadService {
	@Resource
	private PatentUploadDao patentUploadDao;
	
	@Transactional
	public void save(PatentUploadModel model) {
		patentUploadDao.save(model);
	}
	
	public List<PatentUploadModel> list(Pageable pageable){
		return patentUploadDao.findAll(pageable).getContent();
	}
	
	/**
	 * 上传内容
	 * @param result
	 */
	public void uploadXls(List<Map<String,String>> result) {
		
		//定义匹配规则
		String[] rule= {"patentNo","costType"};
		
		if(null!=result) {
			for(Map<String,String> dataRow:result) {
        		PatentUploadModel model=getModel(rule, dataRow);
        		if(null!=model) {
        			save(model);
        		}
        	}
		}
	}
	
	/**
	 * 通过匹配规则封装数据对象
	 * @param rule
	 * @param row
	 * @return
	 */
	private PatentUploadModel getModel(String[] rule,Map<String,String> row) {
		if(null!=row&&row.size()>0) {
			PatentUploadModel model=new PatentUploadModel();
			for(String key:row.keySet()) {
				int cellNum=Integer.parseInt(key);
				if(cellNum<rule.length) {
					String fieldName=rule[cellNum];
					if(null!=fieldName) {
						Field field=ReflectionUtils.findField(PatentUploadModel.class, fieldName);
						ReflectionUtils.makeAccessible(field);
						ReflectionUtils.setField(field, model,formatUploadData(row.get(key)));
					}
				}
			}
			return model;
		}
		return null;
	}
	/**
	 * 格式化上传数据
	 * @param value
	 * @return
	 */
	private Object formatUploadData(String value) {
		if((value.indexOf("国外申请")!=-1)) {
			return "SQ5";
		}else if(value.indexOf("国外授权")!=-1){
			return "SQ6";
		} else if(value.indexOf("授权")!=-1) {
			return "SQ2";
		}else if(value.indexOf("申请")!=-1) {
			return "SQ1";
		}else{
			return value;
		}
	}
}
