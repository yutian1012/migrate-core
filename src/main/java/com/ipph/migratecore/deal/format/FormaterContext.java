package com.ipph.migratecore.deal.format;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ipph.migratecore.deal.exception.FormatException;
import com.ipph.migratecore.model.FormatModel;

@Component
public class FormaterContext {
	
	private List<Formater> formaterList=new ArrayList<>();
	
	public List<Formater> getFormaterList() {
		return formaterList;
	}
	public void setFormaterList(List<Formater> formaterList) {
		this.formaterList = formaterList;
	}
	/**
	 * 格式化数据
	 * @param fieldFormatModel
	 * @param value
	 * @return
	 * @throws Exception 
	 */
	public Object getFormatedValue(FormatModel fieldFormatModel,Object value ) throws FormatException {
		if(null==fieldFormatModel){
			return value;
		}
		
		Formater formater=null;
		try {
			formater = getFormater(fieldFormatModel.getClazz());
			if(null!=formater){
				value=formater.format(fieldFormatModel.getFormatParameter(), value);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new FormatException("format class not exists!!!");
		}
		
		return value;
	}
	/**
	 * 获取格式化处理类
	 * @param formater
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private synchronized Formater getFormater(Class<?> clazz) throws InstantiationException, IllegalAccessException{
		
		Formater formater=null;
		
		for(Formater temp:formaterList) {
			if(temp.getClass().getName()==clazz.getName()) {
				formater=temp;
			}
		}
		
		if(formater==null) {
			formater=(Formater) clazz.newInstance();
			if(null!=formater) {
				formaterList.add(formater);
			}
		}
		
		return formater;
	}
	
}
