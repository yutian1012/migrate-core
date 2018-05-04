package com.ipph.migratecore.deal.condition;

import com.ipph.migratecore.model.ConditionModel;

public class ConditionDefaultImpl implements Condition{

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
