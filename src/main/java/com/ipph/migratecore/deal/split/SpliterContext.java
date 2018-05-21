package com.ipph.migratecore.deal.split;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ipph.migratecore.deal.exception.SplitException;
import com.ipph.migratecore.model.SplitModel;

@Component
public class SpliterContext {
	
	private List<Spliter> spliterList=new ArrayList<>();
	
	public List<Spliter> getSpliterList() {
		return spliterList;
	}
	public void setSpliterList(List<Spliter> spliterList) {
		this.spliterList = spliterList;
	}
	/**
	 * 拆分数据
	 * @param 
	 * @param value
	 * @return
	 * @throws Exception 
	 */
	public List<Object> getSplitedValue(SplitModel splitModel,Object value ) throws SplitException {
		List<Object> result=null;
		if(null==splitModel){
			result=new ArrayList<>(1);
			result.add(value);
			return result;
		}
		
		Spliter spliter=null;
		try {
			spliter = getSpliter(splitModel.getClazz());
			if(null!=spliter){
				result=spliter.split(splitModel.getSplitParameter(), value);
			}
			if(null!=result) {
				if(result.size()==0) {//提供默认值设置
					if(null!=splitModel.getDefaultValue()||!"".equals(splitModel.getDefaultValue())) {
						result.add(splitModel.getDefaultValue());
					}
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SplitException("split class not exists!!!");
		}
		
		return result;
	}
	/**
	 * 获取格式化处理类
	 * @param spliter
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private synchronized Spliter getSpliter(Class<?> clazz) throws InstantiationException, IllegalAccessException{
		
		Spliter spliter=null;
		
		for(Spliter temp:spliterList) {
			if(temp.getClass().getName()==clazz.getName()) {
				spliter=temp;
			}
		}
		
		if(spliter==null) {
			spliter=getSpliterInstance(clazz);
			if(null!=spliter) {
				spliterList.add(spliter);
			}
		}
		
		return spliter;
	}
	/**
	 * 获取对象
	 * @param clazz
	 * @return
	 */
	private Spliter getSpliterInstance(Class<?> clazz) {
		
		if(clazz.getName()==CharacterSpliter.class.getName()) {
			return new CharacterSpliter();
		}
		
		return null;
	}
	
}
