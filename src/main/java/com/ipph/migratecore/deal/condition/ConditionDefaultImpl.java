package com.ipph.migratecore.deal.condition;

import com.ipph.migratecore.model.ConditionModel;

public class ConditionDefaultImpl implements Condition{
	
	private static Condition instance;
	
	private ConditionDefaultImpl() {
	}
	
	public static synchronized Condition getInstance() {
		if(null==instance) {
			instance=new ConditionDefaultImpl();
		}
		return instance;
	}

	@Override
	public String getConditionParam(ConditionModel fieldConditionModel) {
		if(null==fieldConditionModel||null==fieldConditionModel.getType())
			return null;
		
		return fieldConditionModel.getType().getName()+" ? ";
	}

	@Override
	public Object getConditionParamValue(ConditionModel fieldConditionModel) {
		if(null==fieldConditionModel||null==fieldConditionModel.getType())
			return null;
		
		//return fieldConditionModel.getValue();
		return null;
	}

	@Override
	public boolean isValueSkip() {
		return false;
	}

}
