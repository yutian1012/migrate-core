package com.ipph.migratecore.deal.condition;

import java.util.Map;

import com.ipph.migratecore.model.ConditionModel;

public class ConditionIsNullImpl implements Condition{
	
	private static Condition instance;
	
	private ConditionIsNullImpl() {
		
	}
	
	public static synchronized Condition getInstance() {
		if(null==instance) {
			instance=new ConditionIsNullImpl();
		}
		return instance;
	}

	@Override
	public String getConditionParam(ConditionModel fieldConditionModel) {
		if(null==fieldConditionModel||null==fieldConditionModel.getType())
			return null;
		
		return fieldConditionModel.getType().getName();
	}

	@Override
	public Object getConditionParamValue(ConditionModel fieldConditionModel,Map<String,Object> rowData) {
		return null;
	}

	@Override
	public boolean isValueSkip() {
		return true;
	}

}
