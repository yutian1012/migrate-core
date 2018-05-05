package com.ipph.migratecore.deal.condition;

import org.springframework.stereotype.Component;

import com.ipph.migratecore.enumeration.FieldConditionTypeEnum;
import com.ipph.migratecore.model.ConditionModel;

@Component
public class ConditionContext {
	
	/**
	 * 是否忽略获取参数值
	 * 针对 is not null或is null类型的条件
	 * @return
	 */
	public boolean isValueSkip(ConditionModel conditionModel){
		
		if(null==conditionModel) return true;
		
		FieldConditionTypeEnum conditionType=conditionModel.getType();
		
		if(null==conditionType) return true;
		
		Condition condition=conditionType.getConditionHandler();
		
		if(null!=condition){
			return condition.isValueSkip();
		}
		return true;
	}

	/**
	 * 获取条件参数
	 * 针对in的情况还需要解析设置的参数，从而确定出参数的个数，可能会有多个逗号作为占位符
	 * @param condition
	 * @return
	 * @throws ConditionException 
	 */
	public Object getConditionParam(ConditionModel conditionModel){
		return getCondition(conditionModel,true);
	}
	/**
	 * 获取条件的数据，替换sql中的占位符
	 * @param condition
	 * @return
	 * @throws ConditionException 
	 */
	public Object getConditionParamValue(ConditionModel conditionModel){
		return getCondition(conditionModel, false);
	}
	/**
	 * 获取参数信息，
	 * @param condition
	 * @param isParam true表示参数值，false表示参数占位符
	 * @return
	 * @throws ConditionException 
	 */
	private Object getCondition(ConditionModel conditionModel,boolean isParam){
		
		if(null==conditionModel) return null;
		
		FieldConditionTypeEnum conditionType=conditionModel.getType();
		
		if(null==conditionType) {
			return null;
		}
		
		String result=null;
		
		Condition condition=conditionType.getConditionHandler();
		
		if(null!=condition){
			if(isParam){
				return condition.getConditionParam(conditionModel);
			}else{
				return condition.getConditionParamValue(conditionModel);
			}
		}
		
		return result;
	}
}
