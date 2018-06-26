package com.ipph.migratecore.service;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.ipph.migratecore.dao.CopyrightUploadDao;
import com.ipph.migratecore.model.CopyrightUploadModel;
import com.ipph.migratecore.patent.util.DateFormatUtil;

@Service
public class CopyrightUploadService {
	
	@Resource
	private CopyrightUploadDao copyrightUploadDao;

	public String[] copyrightInsert=new String[] {"authorName","ownerName","workName","workNo","finishDate","createDate","type","paperNo",
			"firstPublicDate","firstPublicCity","getRightWay","RightOwnership","RightOwnershipStatus","applyType","applicantName","applyContacts","registFee"};
	/**
	 * 处理上传excel
	 * @param result
	 * @throws ParseException
	 */
	public void uploadXls(List<Map<String,String>> result) throws ParseException {
		
		if(null!=result) {
			for(Map<String,String> dataRow:result) {
				String[] rule=null;
				if(null!=dataRow.get("0")) {
					rule=copyrightInsert;
				}
				if(null==rule) {
					continue;
				}
        		CopyrightUploadModel model=getModel(rule, dataRow);
        		if(null!=model) {
        			save(model);
        		}
        	}
		}
	}
	/**
	 * 上传数据
	 * @param model
	 * @return
	 */
	@Transactional
	public CopyrightUploadModel save(CopyrightUploadModel model) {
		return copyrightUploadDao.save(model);
	}
	
	/**
	 * 获取上传的对象
	 * @param rule
	 * @param row
	 * @return
	 * @throws ParseException
	 */
	private CopyrightUploadModel getModel(String[] rule,Map<String,String> row) throws ParseException {
		if(null!=row&&row.size()>0) {
			CopyrightUploadModel model=new CopyrightUploadModel();
			for(String key:row.keySet()) {
				int cellNum=Integer.parseInt(key);
				if(cellNum<rule.length) {
					String fieldName=rule[cellNum];
					if(null!=fieldName) {
						if(null==row.get(key)||"".equals(row.get(key))) {
							continue;
						}
						Field field=ReflectionUtils.findField(CopyrightUploadModel.class, fieldName);
						Object value=row.get(key);
						if(field.getType() == Date.class) {
							try {
								value=DateFormatUtil.parse(row.get(key),"yyyy-MM-dd");
							}catch (Exception e) {
								value=DateFormatUtil.parse(row.get(key),"yyyy/MM/dd");
							}
						}else if(field.getType() == Integer.class){
							value=Integer.parseInt(row.get(key));
						}else if(field.getType()==Long.class){
							value=Long.parseLong(row.get(key));
						}else {
							//value=formatUploadData(row.get(key),field.getName());
						}
						ReflectionUtils.makeAccessible(field);
						ReflectionUtils.setField(field, model,value);
					}
				}
			}
			
			//处理国家
			//processCountry(model);
			
			return model;
		}
		return null;
	}
}
