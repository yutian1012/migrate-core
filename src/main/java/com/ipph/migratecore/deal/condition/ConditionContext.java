package com.ipph.migratecore.deal.condition;

import java.util.List;

import com.ipph.migratecore.enumeration.FieldConditionTypeEnum;
import com.ipph.migratecore.model.FieldModel;

public class ConditionContext {
	
	private List<Condition> conditionList;
	
	
	public List<Condition> getConditionList() {
		return conditionList;
	}

	public void setConditionList(List<Condition> conditionList) {
		this.conditionList = conditionList;
	}
	/**
	 * 是否忽略获取参数值
	 * 针对 is not null或is null类型的条件
	 * @return
	 */
	public boolean isValueSkip(FieldModel field){
		
		if(null==field.getCondition()) return true;
		
		FieldConditionTypeEnum conditionType=field.getCondition().getConditionType();
		
		Condition condition=getCondition(conditionType);
		
		if(null!=condition){
			return condition.isValueSkip();
		}
		return true;
	}

	/**
	 * 获取条件参数
	 * 针对in的情况还需要解析设置的参数，从而确定出参数的个数，可能会有多个逗号作为占位符
	 * @param field
	 * @return
	 * @throws ConditionException 
	 */
	public String getConditionParam(FieldModel field){
		return (String) getConditionParamInfo(field,true);
	}
	/**
	 * 获取条件的数据，替换sql中的占位符
	 * @param field
	 * @return
	 * @throws ConditionException 
	 */
	public Object getConditionParamValue(FieldModel field){
		return getConditionParamInfo(field, false);
	}
	/**
	 * 获取参数信息，
	 * @param field
	 * @param isParam true表示参数值，false表示参数占位符
	 * @return
	 * @throws ConditionException 
	 */
	private Object getConditionParamInfo(FieldModel field,boolean isParam){
		
		if(null==field.getCondition()) return null;
		
		FieldConditionTypeEnum conditionType=field.getCondition().getConditionType();
		
		String result=null;
		
		Condition condition=getCondition(conditionType);
		
		if(null!=condition){
			if(isParam){
				return condition.getConditionParam(field.getCondition());
			}else{
				return condition.getConditionParamValue(field.getCondition());
			}
		}
		
		return result;
	}
	
	/**
	 * 获取condition的处理类
	 * 有新的处理类需要在此进行配置
	 * @param conditionType
	 * @return
	 */
	private Condition getCondition(FieldConditionTypeEnum conditionType){
		Condition condition=null;
		switch (conditionType) {
		case IN:
			condition=getCondition(ConditionInImpl.class);
			break;
		case NOTIN:
			condition=getCondition(ConditionNotInImpl.class);
			break;
		case ISNOTNULL:
			condition=getCondition(ConditionIsNotNullImpl.class);
		break;
		case ISNULL:
			condition=getCondition(ConditionIsNullImpl.class);
		break;
		default:
			condition=getCondition(ConditionDefaultImpl.class);
			break;
		}
		return condition;
	}
	/**
	 * 获取处理类实例
	 * @param clazz
	 * @return
	 */
	private Condition getCondition(Class<?> clazz){
		if(null==conditionList) return null;

		for(Condition c:conditionList){
			if(c.getClass().getName().equals(clazz.getName())){
				return c;
			}
		}
		return null;
	}
}
