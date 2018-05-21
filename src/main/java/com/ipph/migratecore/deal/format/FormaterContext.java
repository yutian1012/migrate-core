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
			if(null==value||"".equals(value)) {//提供默认值设置
				if(null!=fieldFormatModel.getDefaultValue()||!"".equals(fieldFormatModel.getDefaultValue())) {
					value=fieldFormatModel.getDefaultValue();
				}
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
			formater=getFormaterInstance(clazz);
			if(null!=formater) {
				formaterList.add(formater);
			}
		}
		
		return formater;
	}
	/**
	 * 获取对象
	 * @param clazz
	 * @return
	 */
	private Formater getFormaterInstance(Class<?> clazz) {
		if(clazz.getName()==PatentNoFormater.class.getName()) {
			return new PatentNoFormater();
		}
		if(clazz.getName()==PctNoFormater.class.getName()) {
			return new PctNoFormater();
		}
		if(clazz.getName()==JsonMethodFormater.class.getName()) {
			return new JsonMethodFormater();
		}
		return null;
	}
	
}
